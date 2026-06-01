package smarthouse.com.mqtt

import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class MqttService {

    private val client: Mqtt3BlockingClient = Mqtt3Client.builder()
        .serverHost("c5d8a79149ab41d89acfe6fd0306a95b.s1.eu.hivemq.cloud")
        .serverPort(8883)
        .sslWithDefaultConfig()
        .simpleAuth()
            .username("Broker")
            .password("Fatec2026".toByteArray())
            .applySimpleAuth()
        .identifier("SpringService_${System.currentTimeMillis()}")
        .buildBlocking()

    init {
        client.connectWith().cleanSession(true).send()
    }

    fun publish(topic: String, payload: String) {
        client.publishWith()
            .topic(topic)
            .payload(payload.toByteArray(StandardCharsets.UTF_8))
            .send()
    }

    fun ligarVentilador()    = publish("casa/command/ventilador", "ON")
    fun desligarVentilador() = publish("casa/command/ventilador", "OFF")
    fun ligarLuz()           = publish("casa/command/led_branco", "ON")
    fun desligarLuz()        = publish("casa/command/led_branco", "OFF")
    fun setThresholdTemp(valor: Float) = publish("casa/command/threshold/temp", valor.toString())
    fun setThresholdLdr(valor: Int)    = publish("casa/command/threshold/ldr", valor.toString())
    fun bloquearAutoTemp(bloquear: Boolean) = publish("casa/command/bloqueio_sensor_temp", if (bloquear) "ON" else "OFF")
    fun bloquearAutoLuz(bloquear: Boolean)  = publish("casa/command/bloqueio_sensor_luz",  if (bloquear) "ON" else "OFF")
}