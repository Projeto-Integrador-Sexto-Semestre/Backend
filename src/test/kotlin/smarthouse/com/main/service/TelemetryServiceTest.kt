package smarthouse.com.main.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.* // Importa tudo do mockito-kotlin para facilitar
import smarthouse.com.main.model.*
import smarthouse.com.main.repository.*
import java.util.*

@ExtendWith(MockitoExtension::class)
@DisplayName("TelemetryService - Processamento de Telemetria MQTT")
class TelemetryServiceTest {

    @Mock private lateinit var sensorRepository: SensorRepository
    @Mock private lateinit var historyRepository: SensorHistoryRepository
    @Mock private lateinit var eventLogRepository: EventLogRepository
    @Mock private lateinit var notificationRepository: NotificationRepository

    @InjectMocks
    private lateinit var telemetryService: TelemetryService

    private lateinit var testUser: User
    private lateinit var testHouse: House
    private lateinit var testRoom: Room
    private lateinit var testSensor: Sensor
    private lateinit var testDeviceType: DeviceType

    @BeforeEach
    fun setUp() {
        testUser = User(1L, "user@test.com", "password", "Test User")
        testHouse = House(1L, "Casa Teste", "Rua Teste", testUser)
        testRoom = Room(1L, "Sala", "SALA", testHouse)
        testDeviceType = DeviceType(1L, "Sensor", "Teste", "C")
        testSensor = Sensor(1L, "Sensor Sala", "sensor/1", testDeviceType, testRoom)
    }

    @Nested
    @DisplayName("Fluxo com Dados Válidos")
    inner class FluxoComDadosValidos {

        @Test
        @DisplayName("deveProcessarMensagem_comMultiplosSensores")
        fun deveProcessarMensagemComMultiplosSensores() {
            val sensor2 = Sensor(2L, "Sensor Cozinha", "sensor/2", testDeviceType, testRoom)

            whenever(sensorRepository.findById(1L)).thenReturn(Optional.of(testSensor))
            whenever(sensorRepository.findById(2L)).thenReturn(Optional.of(sensor2))

            telemetryService.processarMensagem("sensor/1/temp", "22.0")
            telemetryService.processarMensagem("sensor/2/temp", "23.0")

            // CORREÇÃO: Como o serviço foi chamado 2 vezes,
            // precisamos verificar que o save foi chamado 2 vezes.
            verify(historyRepository, times(2)).save(any())
            verify(eventLogRepository, times(2)).save(any())
        }
    }

    @Nested
    @DisplayName("Validação de Entrada")
    inner class ValidacaoDeEntrada {

        @Test
        @DisplayName("deveNaoProcessarMensagem_comTopicInvalido")
        fun deveNaoProcessarMensagemComTopicInvalido() {
            // CORREÇÃO: Se o tópico é inválido, o serviço pode nem chegar a consultar o repository.
            // Se o seu serviço não consulta, remova o verify(sensorRepository).findById(any())
            // Se o serviço consulta e para, o verify(never()) está correto.
            telemetryService.processarMensagem("topico/invalido", "25.5")

            verify(historyRepository, never()).save(any())
        }
    }
}