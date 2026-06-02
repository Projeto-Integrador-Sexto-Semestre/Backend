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
import smarthouse.com.main.model.*
import smarthouse.com.main.repository.AlertRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testes unitários para AlertController
 *
 * Objetivo: Testar operações CRUD de alertas com ações, sensores e tipos de alerta relacionados
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("AlertController - Gerenciamento de Alertas")
class AlertControllerTest {

    @Mock
    private lateinit var alertRepository: AlertRepository

    private lateinit var alertController: AlertController

    private lateinit var testAlert: Alert
    private lateinit var testAlertType: AlertType
    private lateinit var testSensor: Sensor
    private lateinit var testDevice: IotDevice
    private lateinit var testRoom: Room
    private lateinit var testDeviceType: DeviceType

    @BeforeEach
    fun setUp() {
        alertController = AlertController(alertRepository)

        // Mock utilizado para simular retorno do repositório
        testAlertType = AlertType(
            id = 1L,
            name = "TEMPERATURA_ALTA",
            description = "Alerta de temperatura elevada"
        )

        testDeviceType = DeviceType(
            id = 1L,
            name = "Sensor de Temperatura",
            manufacturer = "DHT22",
            unit = "°C"
        )

        testRoom = Room(
            id = 1L,
            name = "Sala",
            type = "SALA",
            house = null
        )

        // Stub utilizado para fornecer dados controlados
        testSensor = Sensor(
            id = 1L,
            name = "Sensor Temperatura Sala",
            mqttTopic = "casa/sala/temperatura",
            deviceType = testDeviceType,
            room = testRoom
        )

        testDevice = IotDevice(
            id = 1L,
            name = "Ar Condicionado",
            deviceType = testDeviceType,
            topic = "casa/sala/ac",
            status = "OFF",
            room = testRoom
        )

        testAlert = Alert(
            id = 1L,
            message = "Temperatura acima de 30°C",
            timestamp = LocalDateTime.now(),
            acknowledged = false,
            alertType = testAlertType,
            sensor = testSensor,
            device = testDevice
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class ListagensTests {

        @Test
        @DisplayName("deveListarTodosOsAlertas")
        fun deveListarTodosOsAlertas() {
            // Given
            val alertas = listOf(testAlert)
            whenever(alertRepository.findAll()).thenReturn(alertas)

            // When
            val resultado = alertController.list()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("Temperatura acima de 30°C", resultado[0].message)
            assertEquals(false, resultado[0].acknowledged)
            verify(alertRepository).findAll()
        }

        @Test
        @DisplayName("deveListarAlertasVazio")
        fun deveListarAlertasVazio() {
            // Given
            whenever(alertRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = alertController.list()

            // Then
            assertEquals(0, resultado.size)
            verify(alertRepository).findAll()
        }

        @Test
        @DisplayName("deveListarMultiplosAlertas")
        fun deveListarMultiplosAlertas() {
            // Given
            val alerta2 = Alert(
                id = 2L,
                message = "Umidade baixa",
                timestamp = LocalDateTime.now().minusHours(1),
                acknowledged = true,
                alertType = testAlertType,
                sensor = testSensor,
                device = null
            )
            val alertas = listOf(testAlert, alerta2)
            whenever(alertRepository.findAll()).thenReturn(alertas)

            // When
            val resultado = alertController.list()

            // Then
            assertEquals(2, resultado.size)
            assertEquals(false, resultado[0].acknowledged)
            assertEquals(true, resultado[1].acknowledged)
        }
    }

    @Nested
    @DisplayName("Operações de Criação")
    inner class CriacaoTests {

        @Test
        @DisplayName("deveCriarUmAlerta")
        fun deveCriarUmAlerta() {
            // Given
            whenever(alertRepository.save(any())).thenReturn(testAlert)

            // When
            val resultado = alertController.create(testAlert)

            // Then
            assertNotNull(resultado)
            assertEquals("Temperatura acima de 30°C", resultado.message)
            assertEquals(false, resultado.acknowledged)
            verify(alertRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarAlertaSemSensor")
        fun deveCriarAlertaSemSensor() {
            // Given
            val alertaSemSensor = Alert(
                id = null,
                message = "Alerta genérico",
                timestamp = LocalDateTime.now(),
                acknowledged = false,
                alertType = testAlertType,
                sensor = null,
                device = testDevice
            )
            whenever(alertRepository.save(any())).thenReturn(alertaSemSensor)

            // When
            val resultado = alertController.create(alertaSemSensor)

            // Then
            assertNotNull(resultado)
            assertEquals(null, resultado.sensor)
            assertEquals(testDevice.id, resultado.device?.id)
            verify(alertRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarAlertaSemDispositivo")
        fun deveCriarAlertaSemDispositivo() {
            // Given
            val alertaSemDispositivo = Alert(
                id = null,
                message = "Alerta apenas sensor",
                timestamp = LocalDateTime.now(),
                acknowledged = false,
                alertType = testAlertType,
                sensor = testSensor,
                device = null
            )
            whenever(alertRepository.save(any())).thenReturn(alertaSemDispositivo)

            // When
            val resultado = alertController.create(alertaSemDispositivo)

            // Then
            assertNotNull(resultado)
            assertEquals(null, resultado.device)
            assertEquals(testSensor.id, resultado.sensor?.id)
        }

        @Test
        @DisplayName("deveCriarAlertaAcknowledged")
        fun deveCriarAlertaAcknowledged() {
            // Given
            val alertaAcknowledged = Alert(
                id = null,
                message = "Alerta já confirmado",
                timestamp = LocalDateTime.now().minusHours(2),
                acknowledged = true,
                alertType = testAlertType,
                sensor = testSensor,
                device = testDevice
            )
            whenever(alertRepository.save(any())).thenReturn(alertaAcknowledged)

            // When
            val resultado = alertController.create(alertaAcknowledged)

            // Then
            assertNotNull(resultado)
            assertEquals(true, resultado.acknowledged)
        }
    }

    @Nested
    @DisplayName("Operações de Atualização")
    inner class AtualizacaoTests {

        @Test
        @DisplayName("deveAtualizarUmAlerta")
        fun deveAtualizarUmAlerta() {
            // Given
            val alertaAtualizado = Alert(
                id = 1L,
                message = "Temperatura acima de 35°C",
                timestamp = LocalDateTime.now(),
                acknowledged = true,
                alertType = testAlertType,
                sensor = testSensor,
                device = testDevice
            )
            whenever(alertRepository.findById(1L)).thenReturn(Optional.of(testAlert))
            whenever(alertRepository.save(any())).thenReturn(alertaAtualizado)

            // When
            val resultado = alertController.update(1L, alertaAtualizado)

            // Then
            assertNotNull(resultado)
            assertEquals("Temperatura acima de 35°C", resultado.message)
            assertEquals(true, resultado.acknowledged)
            verify(alertRepository).findById(1L)
            verify(alertRepository).save(any())
        }

        @Test
        @DisplayName("deveAtualizarStatusAcknowledged")
        fun deveAtualizarStatusAcknowledged() {
            // Given
            val alertaAcknowledged = Alert(
                id = 1L,
                message = testAlert.message,
                timestamp = testAlert.timestamp,
                acknowledged = true,
                alertType = testAlert.alertType,
                sensor = testAlert.sensor,
                device = testAlert.device
            )
            whenever(alertRepository.findById(1L)).thenReturn(Optional.of(testAlert))
            whenever(alertRepository.save(any())).thenReturn(alertaAcknowledged)

            // When
            val resultado = alertController.update(1L, alertaAcknowledged)

            // Then
            assertNotNull(resultado)
            assertEquals(true, resultado.acknowledged)
        }

        @Test
        @DisplayName("deveAtualizarRemovendoSensor")
        fun deveAtualizarRemovendoSensor() {
            // Given
            val alertaSemSensor = Alert(
                id = 1L,
                message = testAlert.message,
                timestamp = testAlert.timestamp,
                acknowledged = testAlert.acknowledged,
                alertType = testAlert.alertType,
                sensor = null,
                device = testAlert.device
            )
            whenever(alertRepository.findById(1L)).thenReturn(Optional.of(testAlert))
            whenever(alertRepository.save(any())).thenReturn(alertaSemSensor)

            // When
            val resultado = alertController.update(1L, alertaSemSensor)

            // Then
            assertNotNull(resultado)
            assertEquals(null, resultado.sensor)
        }

        @Test
        @DisplayName("deveAtualizarRemovendoSensor")
        fun deveAtualizarRemovendoSensor() {
            // Given
            val alertaSemSensor = Alert(
                id = 1L,
                message = testAlert.message,
                timestamp = testAlert.timestamp,
                acknowledged = testAlert.acknowledged,
                alertType = testAlert.alertType,
                sensor = null,
                device = testAlert.device
            )
            whenever(alertRepository.findById(1L)).thenReturn(Optional.of(testAlert))
            whenever(alertRepository.save(any())).thenReturn(alertaSemSensor)

            // When
            val resultado = alertController.update(1L, alertaSemSensor)

            // Then
            assertNotNull(resultado)
            assertEquals(null, resultado.sensor)
        }
    }

    @Nested
    @DisplayName("Operações de Deleção")
    inner class DelecaoTests {

        @Test
        @DisplayName("deveDeletarUmAlerta")
        fun deveDeletarUmAlerta() {
            // When
            alertController.delete(1L)

            // Then
            verify(alertRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarAlertaComIdElevado")
        fun deveDeletarAlertaComIdElevado() {
            // When
            alertController.delete(999999L)

            // Then
            verify(alertRepository).deleteById(999999L)
        }

        @Test
        @DisplayName("deveDeletarMultiplosAlertas")
        fun deveDeletarMultiplosAlertas() {
            // When
            alertController.delete(1L)
            alertController.delete(2L)
            alertController.delete(3L)

            // Then
            verify(alertRepository).deleteById(1L)
            verify(alertRepository).deleteById(2L)
            verify(alertRepository).deleteById(3L)
        }
    }
}

