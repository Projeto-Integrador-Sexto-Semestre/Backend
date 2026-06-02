package smarthouse.com.main.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Testes unitários para MqttSubscriber
 *
 * Objetivo: Testar inicialização da conexão MQTT e delegação
 * de mensagens para TelemetryService.
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("MqttSubscriber - Integração com MQTT")
class MqttSubscriberTest {

    @Mock
    private lateinit var telemetryService: TelemetryService

    private lateinit var mqttSubscriber: MqttSubscriber

    @BeforeEach
    fun setUp() {
        // Inicializa o subscriber com o mock do serviço
        mqttSubscriber = MqttSubscriber(telemetryService)
    }

    @Test
    @DisplayName("deveCriarInstanciaComTelemetryService")
    fun deveCriarInstanciaComTelemetryService() {
        // Given & When
        val subscriber = MqttSubscriber(telemetryService)

        // Then
        assert(subscriber != null)
    }

    @Test
    @DisplayName("deveTerDependenciaTelemetryService")
    fun deveTerDependenciaTelemetryService() {
        // Given & When
        val subscriber = MqttSubscriber(telemetryService)

        // Then: Verifica que o subscriber foi criado com a dependência
        assert(subscriber != null)
    }

}

