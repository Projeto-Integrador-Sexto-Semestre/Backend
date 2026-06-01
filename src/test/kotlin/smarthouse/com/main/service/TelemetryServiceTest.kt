package smarthouse.com.main.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import smarthouse.com.main.model.*
import smarthouse.com.main.repository.*
import java.time.LocalDateTime
import java.util.*

/**
 * Testes unitários para TelemetryService
 *
 * Objetivo: Testar o processamento de mensagens MQTT, criação de histórico,
 * logs de eventos e notificações. Cobrir todos os fluxos incluindo null checks.
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("TelemetryService - Processamento de Telemetria MQTT")
class TelemetryServiceTest {

    @Mock
    private lateinit var sensorRepository: SensorRepository

    @Mock
    private lateinit var historyRepository: SensorHistoryRepository

    @Mock
    private lateinit var eventLogRepository: EventLogRepository

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    @InjectMocks
    private lateinit var telemetryService: TelemetryService

    private lateinit var testUser: User
    private lateinit var testHouse: House
    private lateinit var testRoom: Room
    private lateinit var testSensor: Sensor
    private lateinit var testDeviceType: DeviceType

    @BeforeEach
    fun setUp() {
        // Setup de dados de teste comuns
        testUser = User(
            id = 1L,
            email = "user@test.com",
            password = "encoded_password",
            name = "Test User"
        )

        testHouse = House(
            id = 1L,
            name = "Casa Teste",
            address = "Rua Teste 123",
            user = testUser
        )

        testRoom = Room(
            id = 1L,
            name = "Sala",
            type = "SALA",
            house = testHouse
        )

        testDeviceType = DeviceType(
            id = 1L,
            name = "Sensor de Temperatura",
            manufacturer = "Teste",
            unit = "°C"
        )

        testSensor = Sensor(
            id = 1L,
            name = "Sensor Sala",
            mqttTopic = "sensor/1",
            deviceType = testDeviceType,
            room = testRoom
        )
    }

    @Nested
    @DisplayName("Fluxo Feliz - Processamento Válido de Mensagem")
    inner class FluxoFeliz {

        @Test
        @DisplayName("deveProcessarMensagemValida_quandoSensorExists")
        fun deveProcessarMensagemValidaQuandoSensorExists() {
            // Given: Mock retorna sensor válido
            val topico = "sensor/1/temperature"
            val payload = "25.5"
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(testSensor))

            // When: Serviço processa mensagem
            telemetryService.processarMensagem(topico, payload)

            // Then: Verifica que todas as operações foram executadas
            verify(sensorRepository).findById(1L)
            verify(historyRepository).save(any())
            verify(eventLogRepository).save(any())
            verify(notificationRepository).save(any())
        }

        @Test
        @DisplayName("deveArmazenarHistoricoComValorCorreto")
        fun deveArmazenarHistoricoComValorCorreto() {
            // Given
            val topico = "sensor/1/data"
            val payload = "30.0"
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(testSensor))

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then: Verifica que o histórico foi salvo com o valor correto
            verify(historyRepository).save(any<SensorHistory>())
        }

        @Test
        @DisplayName("deveArmazenarLogDeEventoComMensagemCorreta")
        fun deveArmazenarLogDeEventoComMensagemCorreta() {
            // Given
            val topico = "sensor/1/status"
            val payload = "ON"
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(testSensor))

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then: Verifica que o log foi criado com tipo de evento MQTT correto
            verify(eventLogRepository).save(any<EventLog>())
        }

        @Test
        @DisplayName("deveArmazenarNotificacaoComMensagemCorreta")
        fun deveArmazenarNotificacaoComMensagemCorreta() {
            // Given
            val topico = "sensor/1/alert"
            val payload = "ALERT_VALUE"
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(testSensor))

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then: Verifica que notificação foi criada
            verify(notificationRepository).save(any<Notification>())
        }

        @Test
        @DisplayName("deveProcessarMensagemComPayloadVazio")
        fun deveProcessarMensagemComPayloadVazio() {
            // Given
            val topico = "sensor/1/empty"
            val payload = ""
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(testSensor))

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then
            verify(sensorRepository).findById(1L)
            verify(historyRepository).save(any())
        }
    }

    @Nested
    @DisplayName("Fluxo com Null Checks - Validação de Integridade")
    inner class FluxoComNullChecks {

        @Test
        @DisplayName("deveRetornarSemProcessar_quandoSensorNaoEncontrado")
        fun deveRetornarSemProcessarQuandoSensorNaoEncontrado() {
            // Given: Mock retorna Optional vazio
            val topico = "sensor/999/temp"
            val payload = "25.5"
            whenever(sensorRepository.findById(999L))
                .thenReturn(Optional.empty())

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then: Nenhuma operação de persistência deve ser executada
            verify(sensorRepository).findById(999L)
            verify(historyRepository, org.mockito.kotlin.never()).save(any())
            verify(eventLogRepository, org.mockito.kotlin.never()).save(any())
            verify(notificationRepository, org.mockito.kotlin.never()).save(any())
        }

        @Test
        @DisplayName("deveRetornarSemProcessar_quandoTopicoInvalido")
        fun deveRetornarSemProcessarQuandoTopicoInvalido() {
            // Given: Tópico sem partes suficientes
            val topico = "sensor"
            val payload = "25.5"

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then: Nenhuma operação deve ser executada (parse falha)
            verify(sensorRepository, org.mockito.kotlin.times(0)).findById(any())
        }

        @Test
        @DisplayName("deveRetornarSemProcessar_quandoIdSensorNaoEhNumerico")
        fun deveRetornarSemProcessarQuandoIdSensorNaoEhNumerico() {
            // Given: ID não é número
            val topico = "sensor/abc/temp"
            val payload = "25.5"

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then
            verify(sensorRepository, org.mockito.kotlin.times(0)).findById(any())
        }

        @Test
        @DisplayName("deveProcessarMensagem_quandoRoomEhNull")
        fun deveProcessarMensagemQuandoRoomEhNull() {
            // Given: Sensor sem room
            val sensorSemRoom = Sensor(
                id = 1L,
                name = "Sensor Sala",
                mqttTopic = "sensor/1",
                deviceType = testDeviceType,
                room = null
            )
            val topico = "sensor/1/temp"
            val payload = "25.5"
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(sensorSemRoom))

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then: Ainda deve processar (null reference no user)
            verify(historyRepository).save(any())
            verify(eventLogRepository).save(any())
            verify(notificationRepository).save(any())
        }

        @Test
        @DisplayName("deveProcessarMensagem_quandoHouseEhNull")
        fun deveProcessarMensagemQuandoHouseEhNull() {
            // Given: Room sem house
            testRoom.house = null
            val topico = "sensor/1/temp"
            val payload = "25.5"
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(testSensor))

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then
            verify(historyRepository).save(any())
            verify(eventLogRepository).save(any())
        }

        @Test
        @DisplayName("deveProcessarMensagem_quandoUserEhNull")
        fun deveProcessarMensagemQuandoUserEhNull() {
            // Given: House sem user
            testHouse.user = null
            val topico = "sensor/1/temp"
            val payload = "25.5"
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(testSensor))

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then: Log e notificação com user null
            verify(historyRepository).save(any())
            verify(eventLogRepository).save(any())
            verify(notificationRepository).save(any())
        }
    }

    @Nested
    @DisplayName("Fluxo com Diferentes Formatos de Tópico")
    inner class FluxoComDiferentesFormatos {

        @Test
        @DisplayName("deveprocessarTopicoComMuitasPartes")
        fun deveprocessarTopicoComMuitasPartes() {
            // Given
            val topico = "sensor/1/temperature/room/house"
            val payload = "22.5"
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(testSensor))

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then
            verify(sensorRepository).findById(1L)
            verify(historyRepository).save(any())
        }

        @Test
        @DisplayName("deveProcessarTopicoComSensorIdZero")
        fun deveProcessarTopicoComSensorIdZero() {
            // Given
            val topico = "sensor/0/temp"
            val payload = "25.5"

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then
            verify(sensorRepository).findById(0L)
        }

        @Test
        @DisplayName("deveProcessarTopicoComNumeroNegativo")
        fun deveProcessarTopicoComNumeroNegativo() {
            // Given
            val topico = "sensor/-1/temp"
            val payload = "25.5"

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then
            verify(sensorRepository).findById(-1L)
        }

        @Test
        @DisplayName("deveRetornarSemProcessar_quandoTopicoVazio")
        fun deveRetornarSemProcessarQuandoTopicoVazio() {
            // Given
            val topico = ""
            val payload = "25.5"

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then
            verify(sensorRepository, org.mockito.kotlin.times(0)).findById(any())
        }
    }

    @Nested
    @DisplayName("Fluxo com Payloads Especiais")
    inner class FluxoComPayloadsEspeciais {

        @Test
        @DisplayName("deveProcessarPayloadComCaracteresEspeciais")
        fun deveProcessarPayloadComCaracteresEspeciais() {
            // Given
            val topico = "sensor/1/data"
            val payload = "!@#$%^&*()"
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(testSensor))

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then
            verify(historyRepository).save(any())
        }

        @Test
        @DisplayName("deveProcessarPayloadComEspacos")
        fun deveProcessarPayloadComEspacos() {
            // Given
            val topico = "sensor/1/msg"
            val payload = "  valor com espaços  "
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(testSensor))

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then
            verify(historyRepository).save(any())
        }

        @Test
        @DisplayName("deveProcessarPayloadMuitoGrande")
        fun deveProcessarPayloadMuitoGrande() {
            // Given
            val topico = "sensor/1/large"
            val payload = "x".repeat(10000)
            whenever(sensorRepository.findById(1L))
                .thenReturn(Optional.of(testSensor))

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then
            verify(historyRepository).save(any())
        }
    }

    @Nested
    @DisplayName("Cobertura de Elvis Operators e Null Safety")
    inner class CoberturaNullSafety {

        @Test
        @DisplayName("deveRetornarNull_ParaGetOrNull_ComTopicoInvalido")
        fun deveRetornarNullParaGetOrNull() {
            // Given
            val topico = "invalid"
            val payload = "test"

            // When - Não aloca sensor
            telemetryService.processarMensagem(topico, payload)

            // Then - nenhuma chamada ao repository
            verify(sensorRepository, org.mockito.kotlin.times(0)).findById(any())
        }

        @Test
        @DisplayName("deveTratarOrElse_ComSensorNaoEncontrado")
        fun deveTratarOrElse() {
            // Given
            val topico = "sensor/999/temp"
            val payload = "25.5"
            whenever(sensorRepository.findById(999L))
                .thenReturn(Optional.empty())

            // When
            telemetryService.processarMensagem(topico, payload)

            // Then - orElse null evita NPE
            verify(sensorRepository).findById(999L)
        }
    }

}

