package smarthouse.com.main.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import smarthouse.com.main.model.IotDevice
import smarthouse.com.main.model.DeviceType
import smarthouse.com.main.model.Room
import smarthouse.com.main.repository.IotDeviceRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testes unitários para IotDeviceController
 *
 * Objetivo: Testar operações CRUD de dispositivos IoT com filtragem por sala e atualização de status
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("IotDeviceController - Gerenciamento de Dispositivos IoT")
class IotDeviceControllerTest {

    @Mock
    private lateinit var iotDeviceRepository: IotDeviceRepository

    private lateinit var iotDeviceController: IotDeviceController

    private lateinit var testDevice: IotDevice
    private lateinit var testRoom: Room
    private lateinit var testDeviceType: DeviceType

    @BeforeEach
    fun setUp() {
        iotDeviceController = IotDeviceController(iotDeviceRepository)

        // Mock utilizado para simular retorno do repositório
        testDeviceType = DeviceType(
            id = 1L,
            name = "Lâmpada RGB",
            manufacturer = "Philips",
            unit = null
        )

        testRoom = Room(
            id = 1L,
            name = "Sala",
            type = "SALA",
            house = null
        )

        // Stub utilizado para fornecer dados controlados
        testDevice = IotDevice(
            id = 1L,
            name = "Lâmpada Sala 1",
            deviceType = testDeviceType,
            topic = "casa/sala/lampada1",
            status = "OFF",
            room = testRoom
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class ListagensTests {

        @Test
        @DisplayName("deveListarTodosOsDispositivos")
        fun deveListarTodosOsDispositivos() {
            // Given
            val dispositivos = listOf(testDevice)
            whenever(iotDeviceRepository.findAll()).thenReturn(dispositivos)

            // When
            val resultado = iotDeviceController.listAll()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("Lâmpada Sala 1", resultado[0].name)
            assertEquals("OFF", resultado[0].status)
            verify(iotDeviceRepository).findAll()
        }

        @Test
        @DisplayName("deveListarDispositivosVazio")
        fun deveListarDispositivosVazio() {
            // Given
            whenever(iotDeviceRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = iotDeviceController.listAll()

            // Then
            assertEquals(0, resultado.size)
            verify(iotDeviceRepository).findAll()
        }

        @Test
        @DisplayName("deveListarMultiplosDispositivos")
        fun deveListarMultiplosDispositivos() {
            // Given
            val dispositivo2 = IotDevice(
                id = 2L,
                name = "Lâmpada Sala 2",
                deviceType = testDeviceType,
                topic = "casa/sala/lampada2",
                status = "ON",
                room = testRoom
            )
            val dispositivos = listOf(testDevice, dispositivo2)
            whenever(iotDeviceRepository.findAll()).thenReturn(dispositivos)

            // When
            val resultado = iotDeviceController.listAll()

            // Then
            assertEquals(2, resultado.size)
            assertEquals("Lâmpada Sala 1", resultado[0].name)
            assertEquals("OFF", resultado[0].status)
            assertEquals("Lâmpada Sala 2", resultado[1].name)
            assertEquals("ON", resultado[1].status)
        }
    }

    @Nested
    @DisplayName("Operações de Criação")
    inner class CriacaoTests {

        @Test
        @DisplayName("deveCriarUmDispositivo")
        fun deveCriarUmDispositivo() {
            // Given
            whenever(iotDeviceRepository.save(any())).thenReturn(testDevice)

            // When
            val resultado = iotDeviceController.create(testDevice)

            // Then
            assertNotNull(resultado)
            assertEquals("Lâmpada Sala 1", resultado.name)
            assertEquals("OFF", resultado.status)
            verify(iotDeviceRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarDispositivoLigado")
        fun deveCriarDispositivoLigado() {
            // Given
            val dispositivoLigado = IotDevice(
                id = null,
                name = "Lâmpada Ligada",
                deviceType = testDeviceType,
                topic = "casa/sala/lampada_on",
                status = "ON",
                room = testRoom
            )
            whenever(iotDeviceRepository.save(any())).thenReturn(dispositivoLigado)

            // When
            val resultado = iotDeviceController.create(dispositivoLigado)

            // Then
            assertEquals("ON", resultado.status)
        }

        @Test
        @DisplayName("deveCriarDispositivoComStatusCustomizado")
        fun deveCriarDispositivoComStatusCustomizado() {
            // Given
            val dispositivoCustom = IotDevice(
                id = null,
                name = "AC",
                deviceType = testDeviceType,
                topic = "casa/sala/ac",
                status = "TEMP:25",
                room = testRoom
            )
            whenever(iotDeviceRepository.save(any())).thenReturn(dispositivoCustom)

            // When
            val resultado = iotDeviceController.create(dispositivoCustom)

            // Then
            assertEquals("TEMP:25", resultado.status)
        }
    }

    @Nested
    @DisplayName("Operações de Listagem por Sala")
    inner class ListagemPorSalaTests {

        @Test
        @DisplayName("deveListarDispositivosPorSala")
        fun deveListarDispositivosPorSala() {
            // Given
            val dispositivos = listOf(testDevice)
            whenever(iotDeviceRepository.findByRoomId(1L)).thenReturn(dispositivos)

            // When
            val resultado = iotDeviceController.listByRoom(1L)

            // Then
            assertEquals(1, resultado.size)
            assertEquals("Lâmpada Sala 1", resultado[0].name)
            assertEquals(1L, resultado[0].room?.id)
            verify(iotDeviceRepository).findByRoomId(1L)
        }

        @Test
        @DisplayName("deveListarSalaVaziaRetornaVazio")
        fun deveListarSalaVaziaRetornaVazio() {
            // Given
            whenever(iotDeviceRepository.findByRoomId(999L)).thenReturn(emptyList())

            // When
            val resultado = iotDeviceController.listByRoom(999L)

            // Then
            assertEquals(0, resultado.size)
            verify(iotDeviceRepository).findByRoomId(999L)
        }

        @Test
        @DisplayName("deveListarMultiplosDispositivosMesmaSala")
        fun deveListarMultiplosDispositivosMesmaSala() {
            // Given
            val dispositivo2 = IotDevice(
                id = 2L,
                name = "Lâmpada Sala 2",
                deviceType = testDeviceType,
                topic = "casa/sala/lampada2",
                status = "ON",
                room = testRoom
            )
            val dispositivos = listOf(testDevice, dispositivo2)
            whenever(iotDeviceRepository.findByRoomId(1L)).thenReturn(dispositivos)

            // When
            val resultado = iotDeviceController.listByRoom(1L)

            // Then
            assertEquals(2, resultado.size)
            for (device in resultado) {
                assertEquals(1L, device.room?.id)
            }
        }
    }

    @Nested
    @DisplayName("Operações de Atualização de Status")
    inner class AtualizacaoStatusTests {

        @Test
        @DisplayName("deveAtualizarStatusParaON")
        fun deveAtualizarStatusParaON() {
            // Given
            val statusUpdate = mapOf("status" to "ON")
            val dispositivoAtualizado = IotDevice(
                id = 1L,
                name = testDevice.name,
                deviceType = testDevice.deviceType,
                topic = testDevice.topic,
                status = "ON",
                room = testDevice.room
            )
            whenever(iotDeviceRepository.findById(1L)).thenReturn(Optional.of(testDevice))
            whenever(iotDeviceRepository.save(any())).thenReturn(dispositivoAtualizado)

            // When
            val resultado = iotDeviceController.updateStatus(1L, statusUpdate)

            // Then
            assertNotNull(resultado)
            assertEquals("ON", resultado.status)
            verify(iotDeviceRepository).findById(1L)
            verify(iotDeviceRepository).save(any())
        }

        @Test
        @DisplayName("deveAtualizarStatusParaOFF")
        fun deveAtualizarStatusParaOFF() {
            // Given
            val dispositoOn = IotDevice(
                id = 1L,
                name = "Test",
                deviceType = testDeviceType,
                topic = "test",
                status = "ON",
                room = testRoom
            )
            val statusUpdate = mapOf("status" to "OFF")
            val dispositivoAtualizado = IotDevice(
                id = 1L,
                name = dispositoOn.name,
                deviceType = dispositoOn.deviceType,
                topic = dispositoOn.topic,
                status = "OFF",
                room = dispositoOn.room
            )
            whenever(iotDeviceRepository.findById(1L)).thenReturn(Optional.of(dispositoOn))
            whenever(iotDeviceRepository.save(any())).thenReturn(dispositivoAtualizado)

            // When
            val resultado = iotDeviceController.updateStatus(1L, statusUpdate)

            // Then
            assertEquals("OFF", resultado.status)
        }

        @Test
        @DisplayName("deveAtualizarStatusCustomizado")
        fun deveAtualizarStatusCustomizado() {
            // Given
            val statusUpdate = mapOf("status" to "BRIGHTNESS:80")
            val dispositivoAtualizado = IotDevice(
                id = 1L,
                name = testDevice.name,
                deviceType = testDevice.deviceType,
                topic = testDevice.topic,
                status = "BRIGHTNESS:80",
                room = testDevice.room
            )
            whenever(iotDeviceRepository.findById(1L)).thenReturn(Optional.of(testDevice))
            whenever(iotDeviceRepository.save(any())).thenReturn(dispositivoAtualizado)

            // When
            val resultado = iotDeviceController.updateStatus(1L, statusUpdate)

            // Then
            assertEquals("BRIGHTNESS:80", resultado.status)
        }

        @Test
        @DisplayName("deveAtualizarStatusComMapVazio")
        fun deveAtualizarStatusComMapVazio() {
            // Given
            val statusUpdate = emptyMap<String, String>()
            whenever(iotDeviceRepository.findById(1L)).thenReturn(Optional.of(testDevice))
            whenever(iotDeviceRepository.save(any())).thenReturn(testDevice)

            // When
            val resultado = iotDeviceController.updateStatus(1L, statusUpdate)

            // Then
            assertEquals("OFF", resultado.status) // Mantém status anterior
        }
    }

    @Nested
    @DisplayName("Operações de Deleção")
    inner class DelecaoTests {

        @Test
        @DisplayName("deveDeletarUmDispositivo")
        fun deveDeletarUmDispositivo() {
            // When
            iotDeviceController.delete(1L)

            // Then
            verify(iotDeviceRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarMultiplosDispositivos")
        fun deveDeletarMultiplosDispositivos() {
            // When
            iotDeviceController.delete(1L)
            iotDeviceController.delete(2L)
            iotDeviceController.delete(3L)

            // Then
            verify(iotDeviceRepository).deleteById(1L)
            verify(iotDeviceRepository).deleteById(2L)
            verify(iotDeviceRepository).deleteById(3L)
        }

        @Test
        @DisplayName("deveDeletarDispositivoComIdGrande")
        fun deveDeletarDispositivoComIdGrande() {
            // When
            iotDeviceController.delete(999999L)

            // Then
            verify(iotDeviceRepository).deleteById(999999L)
        }
    }
}

