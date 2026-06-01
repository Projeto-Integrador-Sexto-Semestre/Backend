package smarthouse.com.main.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import smarthouse.com.main.model.DeviceType
import smarthouse.com.main.repository.DeviceTypeRepository
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Testes unitários para DeviceTypeController
 *
 * Objetivo: Testar operações CRUD básicas de tipos de dispositivo
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("DeviceTypeController - Gerenciamento de Tipos de Dispositivo")
class DeviceTypeControllerTest {

    @Mock
    private lateinit var deviceTypeRepository: DeviceTypeRepository

    private lateinit var deviceTypeController: DeviceTypeController

    private lateinit var testDeviceType: DeviceType

    @BeforeEach
    fun setUp() {
        deviceTypeController = DeviceTypeController(deviceTypeRepository)

        // Mock utilizado para simular retorno do repositório
        testDeviceType = DeviceType(
            id = 1L,
            name = "Lâmpada RGB",
            manufacturer = "Philips Hue",
            unit = null
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class ListagensTests {

        @Test
        @DisplayName("deveListarTodosTiposDeDispositivo")
        fun deveListarTodosTiposDeDispositivo() {
            // Given
            val tiposDispositivos = listOf(testDeviceType)
            whenever(deviceTypeRepository.findAll()).thenReturn(tiposDispositivos)

            // When
            val resultado = deviceTypeController.list()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("Lâmpada RGB", resultado[0].name)
            assertEquals("Philips Hue", resultado[0].manufacturer)
            verify(deviceTypeRepository).findAll()
        }

        @Test
        @DisplayName("deveListarTiposDispositivoVazio")
        fun deveListarTiposDispositivoVazio() {
            // Given
            whenever(deviceTypeRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = deviceTypeController.list()

            // Then
            assertEquals(0, resultado.size)
            verify(deviceTypeRepository).findAll()
        }

        @Test
        @DisplayName("deveListarMultiplosTiposDeDispositivo")
        fun deveListarMultiplosTiposDeDispositivo() {
            // Given
            val tipoDispositivo2 = DeviceType(
                id = 2L,
                name = "Sensor de Temperatura",
                manufacturer = "DHT22",
                unit = "°C"
            )
            val tipoDispositivo3 = DeviceType(
                id = 3L,
                name = "Ar Condicionado",
                manufacturer = "LG",
                unit = null
            )
            val tiposDispositivos = listOf(testDeviceType, tipoDispositivo2, tipoDispositivo3)
            whenever(deviceTypeRepository.findAll()).thenReturn(tiposDispositivos)

            // When
            val resultado = deviceTypeController.list()

            // Then
            assertEquals(3, resultado.size)
            assertEquals("Lâmpada RGB", resultado[0].name)
            assertEquals("Sensor de Temperatura", resultado[1].name)
            assertEquals("Ar Condicionado", resultado[2].name)
        }

        @Test
        @DisplayName("deveListarTipoDispositivoComUnidade")
        fun deveListarTipoDispositivoComUnidade() {
            // Given
            val tipoComUnidade = DeviceType(
                id = 1L,
                name = "Sensor",
                manufacturer = "Test",
                unit = "°C"
            )
            whenever(deviceTypeRepository.findAll()).thenReturn(listOf(tipoComUnidade))

            // When
            val resultado = deviceTypeController.list()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("°C", resultado[0].unit)
        }

        @Test
        @DisplayName("deveListarTipoDispositivoSemUnidade")
        fun deveListarTipoDispositivoSemUnidade() {
            // Given
            whenever(deviceTypeRepository.findAll()).thenReturn(listOf(testDeviceType))

            // When
            val resultado = deviceTypeController.list()

            // Then
            assertEquals(1, resultado.size)
            assertNull(resultado[0].unit)
        }
    }

    @Nested
    @DisplayName("Operações de Criação")
    inner class CriacaoTests {

        @Test
        @DisplayName("deveCriarUmTipoDeDispositivo")
        fun deveCriarUmTipoDeDispositivo() {
            // Given
            whenever(deviceTypeRepository.save(any())).thenReturn(testDeviceType)

            // When
            val resultado = deviceTypeController.create(testDeviceType)

            // Then
            assertNotNull(resultado)
            assertEquals("Lâmpada RGB", resultado.name)
            assertEquals("Philips Hue", resultado.manufacturer)
            verify(deviceTypeRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarTipoDispositivoComUnidade")
        fun deveCriarTipoDispositivoComUnidade() {
            // Given
            val tipoComUnidade = DeviceType(
                id = null,
                name = "Termômetro",
                manufacturer = "Xiaomi",
                unit = "°C"
            )
            whenever(deviceTypeRepository.save(any())).thenReturn(tipoComUnidade)

            // When
            val resultado = deviceTypeController.create(tipoComUnidade)

            // Then
            assertNotNull(resultado)
            assertEquals("°C", resultado.unit)
            verify(deviceTypeRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarTipoDispositivoSemUnidade")
        fun deveCriarTipoDispositivoSemUnidade() {
            // Given
            val tipoSemUnidade = DeviceType(
                id = null,
                name = "Switch Inteligente",
                manufacturer = "Sonoff",
                unit = null
            )
            whenever(deviceTypeRepository.save(any())).thenReturn(tipoSemUnidade)

            // When
            val resultado = deviceTypeController.create(tipoSemUnidade)

            // Then
            assertNotNull(resultado)
            assertNull(resultado.unit)
        }

        @Test
        @DisplayName("deveCriarMultiplosTiposDeDispositivo")
        fun deveCriarMultiplosTiposDeDispositivo() {
            // Given
            val tipo1 = DeviceType(1L, "Tipo 1", "Fabricante 1", null)
            val tipo2 = DeviceType(2L, "Tipo 2", "Fabricante 2", "unidade")
            whenever(deviceTypeRepository.save(any()))
                .thenReturn(tipo1)
                .thenReturn(tipo2)

            // When
            val resultado1 = deviceTypeController.create(tipo1)
            val resultado2 = deviceTypeController.create(tipo2)

            // Then
            assertEquals("Tipo 1", resultado1.name)
            assertEquals("Tipo 2", resultado2.name)
            verify(deviceTypeRepository).save(tipo1)
            verify(deviceTypeRepository).save(tipo2)
        }

        @Test
        @DisplayName("deveCriarTipoComNomeUnico")
        fun deveCriarTipoComNomeUnico() {
            // Given
            val tipoUnico = DeviceType(
                id = null,
                name = "LÂMPADA_RGB_ESPECIAL",
                manufacturer = "Premium",
                unit = null
            )
            whenever(deviceTypeRepository.save(any())).thenReturn(tipoUnico)

            // When
            val resultado = deviceTypeController.create(tipoUnico)

            // Then
            assertEquals("LÂMPADA_RGB_ESPECIAL", resultado.name)
        }
    }

    @Nested
    @DisplayName("Operações de Deleção")
    inner class DelecaoTests {

        @Test
        @DisplayName("deveDeletarUmTipoDeDispositivo")
        fun deveDeletarUmTipoDeDispositivo() {
            // When
            deviceTypeController.delete(1L)

            // Then
            verify(deviceTypeRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarMultiplosTiposDeDispositivo")
        fun deveDeletarMultiplosTiposDeDispositivo() {
            // When
            deviceTypeController.delete(1L)
            deviceTypeController.delete(2L)
            deviceTypeController.delete(3L)

            // Then
            verify(deviceTypeRepository).deleteById(1L)
            verify(deviceTypeRepository).deleteById(2L)
            verify(deviceTypeRepository).deleteById(3L)
        }

        @Test
        @DisplayName("deveDeletarTipoDispositivoComIdGrande")
        fun deveDeletarTipoDispositivoComIdGrande() {
            // When
            deviceTypeController.delete(999999L)

            // Then
            verify(deviceTypeRepository).deleteById(999999L)
        }
    }
}

