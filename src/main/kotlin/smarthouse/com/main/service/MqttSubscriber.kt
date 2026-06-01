package smarthouse.com.main.service

import jakarta.annotation.PostConstruct
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.springframework.stereotype.Service

@Service
class MqttSubscriber(private val telemetryService: TelemetryService) {

    @PostConstruct
    fun iniciarConexao() {
        val broker = "tcp://localhost:1883" // Endereço do seu broker MQTT
        val clientId = MqttClient.generateClientId()
        val client = MqttClient(broker, clientId)

        client.connect()

        client.subscribe("sensor/#") { topico, mensagem ->
            telemetryService.processarMensagem(topico, mensagem.toString())
        }
    }
}