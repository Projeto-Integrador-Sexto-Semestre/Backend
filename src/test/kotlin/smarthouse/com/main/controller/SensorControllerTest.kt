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
import smarthouse.com.main.model.*
import smarthouse.com.main.repository.SensorRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testes unitários para SensorController
 *
 * Objetivo: Testar operações CRUD de sensores com dispositivos e salas relacionadas
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("SensorController - Gerenciamento de Sensores")
class SensorControllerTest {

    @Mock
    private lateinit var sensorRepository: SensorRepository

    private lateinit var sensorController: SensorController
    private lateinit var testSensor: Sensor
    private lateinit var testDeviceType: DeviceType
    private lateinit var testRoom: Room

    @BeforeEach
    fun setUp() {
        sensorController = SensorController(sensorRepository)

        // Mock utilizado para simular retorno do repositório
        testDeviceType = DeviceType(
            id = 1L,
            name = "Temperatura",
            manufacturer = "DHT22",
            unit = "°C"
        )

        // Stub utilizado para fornecer dados controlados
        testRoom = Room(
            id = 1L,
            name = "Sala",
            type = "SALA",
            house = null
        )

        testSensor = Sensor(
            id = 1L,
            name = "Sensor Temp Sala",
            mqttTopic = "sensor/1/temp",
            deviceType = testDeviceType,
            room = testRoom
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class ListagemTests {

        @Test
        @DisplayName("deveListarTodosSensores")
        fun deveListarTodosSensores() {
            // Given
            val sensores = listOf(testSensor)
            whenever(sensorRepository.findAll()).thenReturn(sensores)

            // When
            val resultado = sensorController.listAll()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("Sensor Temp Sala", resultado[0].name)
            verify(sensorRepository).findAll()
        }

        @Test
        @DisplayName("deveListarSensoresVazio")
        fun deveListarSensoresVazio() {
            // Given
            whenever(sensorRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = sensorController.listAll()

            // Then
            assertEquals(0, resultado.size)
            verify(sensorRepository).findAll()
        }

        @Test
        @DisplayName("deveListarMultiplosSensores")
        fun deveListarMultiplosSensores() {
            // Given
            val sensor2 = Sensor(
                id = 2L,
                name = "Sensor Umidade Sala",
                mqttTopic = "sensor/2/humidity",
                deviceType = testDeviceType,
                room = testRoom
            )
            val sensores = listOf(testSensor, sensor2)
            whenever(sensorRepository.findAll()).thenReturn(sensores)

            // When
            val resultado = sensorController.listAll()

            // Then
            assertEquals(2, resultado.size)
            assertEquals("Sensor Temp Sala", resultado[0].name)
            assertEquals("Sensor Umidade Sala", resultado[1].name)
        }

        @Test
        @DisplayName("deveListarSensoresComTopicoDiferente")
        fun deveListarSensoresComTopicoDiferente() {
            // Given
            val sensor2 = Sensor(
                id = 2L,
                name = "Sensor Quarto",
                mqttTopic = "sensor/quarto/temp",
                deviceType = testDeviceType,
                room = testRoom
            )
            val sensores = listOf(testSensor, sensor2)
            whenever(sensorRepository.findAll()).thenReturn(sensores)

            // When
            val resultado = sensorController.listAll()

            // Then
            assertEquals("sensor/1/temp", resultado[0].mqttTopic)
            assertEquals("sensor/quarto/temp", resultado[1].mqttTopic)
        }
    }

    @Nested
    @DisplayName("Operações de Criação")
    inner class CriacaoTests {

        @Test
        @DisplayName("deveCriarUmSensor")
        fun deveCriarUmSensor() {
            // Given
            whenever(sensorRepository.save(any())).thenReturn(testSensor)

            // When
            val resultado = sensorController.create(testSensor)

            // Then
            assertNotNull(resultado)
            assertEquals("Sensor Temp Sala", resultado.name)
            assertEquals("°C", resultado.deviceType?.unit)
            verify(sensorRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarSensorSemDeviceType")
        fun deveCriarSensorSemDeviceType() {
            // Given
            val sensorSemDeviceType = Sensor(
                id = null,
                name = "Sensor Simples",
                mqttTopic = "test/topic",
                deviceType = null,
                room = testRoom
            )
            whenever(sensorRepository.save(any())).thenReturn(sensorSemDeviceType)

            // When
            val resultado = sensorController.create(sensorSemDeviceType)

            // Then
            assertNotNull(resultado)
            assertEquals(null, resultado.deviceType)
        }

        @Test
        @DisplayName("deveCriarMultiplosSensores")
        fun deveCriarMultiplosSensores() {
            // Given
            val sensor1 = Sensor(1L, "Sensor 1", "topic/1", testDeviceType, testRoom)
            val sensor2 = Sensor(2L, "Sensor 2", "topic/2", testDeviceType, testRoom)
            whenever(sensorRepository.save(any()))
                .thenReturn(sensor1)
                .thenReturn(sensor2)

            // When
            val res1 = sensorController.create(sensor1)
            val res2 = sensorController.create(sensor2)

            // Then
            assertEquals("Sensor 1", res1.name)
            assertEquals("Sensor 2", res2.name)
        }

        @Test
        @DisplayName("deveCriarSensorComTopicoDiferente")
        fun deveCriarSensorComTopicoDiferente() {
            // Given
            val sensorComTopicoDiferente = Sensor(
                id = null,
                name = "Sensor Customizado",
                mqttTopic = "casa/sala/sensor/temperatura",
                deviceType = testDeviceType,
                room = testRoom
            )
            whenever(sensorRepository.save(any())).thenReturn(sensorComTopicoDiferente)

            // When
            val resultado = sensorController.create(sensorComTopicoDiferente)

            // Then
            assertEquals("casa/sala/sensor/temperatura", resultado.mqttTopic)
        }
    }

    @Nested
    @DisplayName("Operações de Deleção")
    inner class DelecaoTests {

        @Test
        @DisplayName("deveDeletarUmSensor")
        fun deveDeletarUmSensor() {
            // When
            sensorController.delete(1L)

            // Then
            verify(sensorRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarMultiplosSensores")
        fun deveDeletarMultiplosSensores() {
            // When
            sensorController.delete(1L)
            sensorController.delete(2L)
            sensorController.delete(3L)

            // Then
            verify(sensorRepository).deleteById(1L)
            verify(sensorRepository).deleteById(2L)
            verify(sensorRepository).deleteById(3L)
        }

        @Test
        @DisplayName("deveDeletarSensorComIdGrande")
        fun deveDeletarSensorComIdGrande() {
            // When
            sensorController.delete(999999L)

            // Then
            verify(sensorRepository).deleteById(999999L)
        }
    }
}

