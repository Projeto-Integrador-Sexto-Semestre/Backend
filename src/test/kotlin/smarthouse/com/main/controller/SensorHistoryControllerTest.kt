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
import smarthouse.com.main.model.SensorHistory
import smarthouse.com.main.model.Sensor
import smarthouse.com.main.model.DeviceType
import smarthouse.com.main.model.Room
import smarthouse.com.main.repository.SensorHistoryRepository
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testes unitários para SensorHistoryController
 *
 * Objetivo: Testar operações de histórico de sensores com ordenação por timestamp
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("SensorHistoryController - Gerenciamento de Histórico de Sensores")
class SensorHistoryControllerTest {

    @Mock
    private lateinit var sensorHistoryRepository: SensorHistoryRepository

    private lateinit var sensorHistoryController: SensorHistoryController

    private lateinit var testSensorHistory: SensorHistory
    private lateinit var testSensor: Sensor
    private lateinit var testRoom: Room
    private lateinit var testDeviceType: DeviceType

    @BeforeEach
    fun setUp() {
        sensorHistoryController = SensorHistoryController(sensorHistoryRepository)

        // Mock utilizado para simular retorno do repositório
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
            name = "Temperatura Sala",
            mqttTopic = "casa/sala/temperatura",
            deviceType = testDeviceType,
            room = testRoom
        )

        testSensorHistory = SensorHistory(
            id = 1L,
            value = "25.5",
            timestamp = LocalDateTime.now(),
            sensor = testSensor
        )
    }

    @Nested
    @DisplayName("Operações de Busca de Histórico")
    inner class BuscaHistoricoTests {

        @Test
        @DisplayName("deveObtorHistoricoDoSensor")
        fun deveObtorHistoricoDoSensor() {
            // Given
            val historicos = listOf(testSensorHistory)
            whenever(sensorHistoryRepository.findBySensorIdOrderByTimestampDesc(1L))
                .thenReturn(historicos)

            // When
            val resultado = sensorHistoryController.getHistory(1L)

            // Then
            assertEquals(1, resultado.size)
            assertEquals("25.5", resultado[0].value)
            assertEquals(1L, resultado[0].sensor?.id)
            verify(sensorHistoryRepository).findBySensorIdOrderByTimestampDesc(1L)
        }

        @Test
        @DisplayName("deveObtorHistoricoVazioParaSensorSemLeituras")
        fun deveObtorHistoricoVazioParaSensorSemLeituras() {
            // Given
            whenever(sensorHistoryRepository.findBySensorIdOrderByTimestampDesc(999L))
                .thenReturn(emptyList())

            // When
            val resultado = sensorHistoryController.getHistory(999L)

            // Then
            assertEquals(0, resultado.size)
            verify(sensorHistoryRepository).findBySensorIdOrderByTimestampDesc(999L)
        }

        @Test
        @DisplayName("deveObtorHistoricoOrdenadoPorTimestampDesc")
        fun deveObtorHistoricoOrdenadoPorTimestampDesc() {
            // Given
            val historico1 = SensorHistory(
                id = 1L,
                value = "25.5",
                timestamp = LocalDateTime.now(),
                sensor = testSensor
            )
            val historico2 = SensorHistory(
                id = 2L,
                value = "25.3",
                timestamp = LocalDateTime.now().minusMinutes(5),
                sensor = testSensor
            )
            val historico3 = SensorHistory(
                id = 3L,
                value = "25.0",
                timestamp = LocalDateTime.now().minusMinutes(10),
                sensor = testSensor
            )
            val historicos = listOf(historico1, historico2, historico3)
            whenever(sensorHistoryRepository.findBySensorIdOrderByTimestampDesc(1L))
                .thenReturn(historicos)

            // When
            val resultado = sensorHistoryController.getHistory(1L)

            // Then
            assertEquals(3, resultado.size)
            // Valida a ordem DESC (mais recente primeiro)
            assertEquals("25.5", resultado[0].value)
            assertEquals("25.3", resultado[1].value)
            assertEquals("25.0", resultado[2].value)
        }

        @Test
        @DisplayName("deveObtorHistoricoComMultiplasLeituras")
        fun deveObtorHistoricoComMultiplasLeituras() {
            // Given
            val historicos = mutableListOf<SensorHistory>()
            for (i in 0 until 100) {
                historicos.add(
                    SensorHistory(
                        id = i.toLong(),
                        value = "25.${i}",
                        timestamp = LocalDateTime.now().minusMinutes(i.toLong()),
                        sensor = testSensor
                    )
                )
            }
            whenever(sensorHistoryRepository.findBySensorIdOrderByTimestampDesc(1L))
                .thenReturn(historicos)

            // When
            val resultado = sensorHistoryController.getHistory(1L)

            // Then
            assertEquals(100, resultado.size)
        }
    }

    @Nested
    @DisplayName("Operações de Salvamento")
    inner class SalvamentoTests {

        @Test
        @DisplayName("deveSalvarUmaLeituraDeSensor")
        fun deveSalvarUmaLeituraDeSensor() {
            // Given
            whenever(sensorHistoryRepository.save(any())).thenReturn(testSensorHistory)

            // When
            val resultado = sensorHistoryController.saveLeitura(testSensorHistory)

            // Then
            assertNotNull(resultado)
            assertEquals("25.5", resultado.value)
            assertEquals(1L, resultado.sensor?.id)
            verify(sensorHistoryRepository).save(any())
        }

        @Test
        @DisplayName("deveSalvarMultiplasLeituras")
        fun deveSalvarMultiplasLeituras() {
            // Given
            val leitura1 = SensorHistory(1L, "25.5", LocalDateTime.now(), testSensor)
            val leitura2 = SensorHistory(2L, "26.0", LocalDateTime.now().plusMinutes(1), testSensor)
            whenever(sensorHistoryRepository.save(any()))
                .thenReturn(leitura1)
                .thenReturn(leitura2)

            // When
            val res1 = sensorHistoryController.saveLeitura(leitura1)
            val res2 = sensorHistoryController.saveLeitura(leitura2)

            // Then
            assertEquals("25.5", res1.value)
            assertEquals("26.0", res2.value)
            verify(sensorHistoryRepository).save(leitura1)
            verify(sensorHistoryRepository).save(leitura2)
        }

        @Test
        @DisplayName("deveSalvarLeituraComValorNegativo")
        fun deveSalvarLeituraComValorNegativo() {
            // Given
            val leituraComValorNegativo = SensorHistory(
                id = null,
                value = "-10.5",
                timestamp = LocalDateTime.now(),
                sensor = testSensor
            )
            whenever(sensorHistoryRepository.save(any())).thenReturn(leituraComValorNegativo)

            // When
            val resultado = sensorHistoryController.saveLeitura(leituraComValorNegativo)

            // Then
            assertEquals("-10.5", resultado.value)
        }

        @Test
        @DisplayName("deveSalvarLeituraComValorAlto")
        fun deveSalvarLeituraComValorAlto() {
            // Given
            val leituraComValorAlto = SensorHistory(
                id = null,
                value = "99.99",
                timestamp = LocalDateTime.now(),
                sensor = testSensor
            )
            whenever(sensorHistoryRepository.save(any())).thenReturn(leituraComValorAlto)

            // When
            val resultado = sensorHistoryController.saveLeitura(leituraComValorAlto)

            // Then
            assertEquals("99.99", resultado.value)
        }

        @Test
        @DisplayName("deveSalvarLeituraComValorVazio")
        fun deveSalvarLeituraComValorVazio() {
            // Given
            val leituraComValorVazio = SensorHistory(
                id = null,
                value = "",
                timestamp = LocalDateTime.now(),
                sensor = testSensor
            )
            whenever(sensorHistoryRepository.save(any())).thenReturn(leituraComValorVazio)

            // When
            val resultado = sensorHistoryController.saveLeitura(leituraComValorVazio)

            // Then
            assertEquals("", resultado.value)
        }

        @Test
        @DisplayName("deveSalvarLeituraComSensorNull")
        fun deveSalvarLeituraComSensorNull() {
            // Given
            val leituraComSensorNull = SensorHistory(
                id = null,
                value = "25.5",
                timestamp = LocalDateTime.now(),
                sensor = null
            )
            whenever(sensorHistoryRepository.save(any())).thenReturn(leituraComSensorNull)

            // When
            val resultado = sensorHistoryController.saveLeitura(leituraComSensorNull)

            // Then
            assertEquals(null, resultado.sensor)
        }

        @Test
        @DisplayName("deveSalvarLeituraComTimestampAnterior")
        fun deveSalvarLeituraComTimestampAnterior() {
            // Given
            val leituraComTimestampAnterior = SensorHistory(
                id = null,
                value = "20.0",
                timestamp = LocalDateTime.of(2020, 1, 1, 12, 0, 0),
                sensor = testSensor
            )
            whenever(sensorHistoryRepository.save(any())).thenReturn(leituraComTimestampAnterior)

            // When
            val resultado = sensorHistoryController.saveLeitura(leituraComTimestampAnterior)

            // Then
            assertEquals(2020, resultado.timestamp.year)
        }
    }
}

