package smarthouse.com.mqtt

import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import smarthouse.com.main.model.SensorHistory
import smarthouse.com.main.repository.SensorHistoryRepository
import smarthouse.com.main.repository.SensorRepository
import java.time.LocalDateTime

@Service
class MqttSubscriberService(
    private val sensorRepository: SensorRepository,
    private val sensorHistoryRepository: SensorHistoryRepository
) {
    private val client: Mqtt3BlockingClient = Mqtt3Client.builder()
        .serverHost("c5d8a79149ab41d89acfe6fd0306a95b.s1.eu.hivemq.cloud")
        .serverPort(8883)
        .sslWithDefaultConfig()
        .simpleAuth()
            .username("Broker")
            .password("Fatec2026".toByteArray())
            .applySimpleAuth()
        .identifier("SpringBackend_${System.currentTimeMillis()}")
        .buildBlocking()

    @PostConstruct
    fun connect() {
        try {
            println("🔌 Conectando ao MQTT...")
            client.connectWith().cleanSession(true).send()
            println("✅ MQTT conectado!")

            // ⚠️ Inicia o listener ANTES de fazer subscribe
            Thread {
                client.publishes(MqttGlobalPublishFilter.ALL).use { publishes ->
                    while (true) {
                        val publish = publishes.receive()
                        val topic = publish.topic.toString()
                        val valor = publish.payload.map { buf ->
                            val bytes = ByteArray(buf.remaining())
                            buf.get(bytes)
                            String(bytes)
                        }.orElse("")
                        println("📨 [$topic] $valor")
                        val sensor = sensorRepository.findByMqttTopic(topic)
                        if (sensor != null) {
                            sensorHistoryRepository.save(
                                SensorHistory(
                                    value = valor,
                                    timestamp = LocalDateTime.now(),
                                    sensor = sensor
                                )
                            )
                        } else {
                            println("⚠️ Sensor não encontrado para tópico: $topic")
                        }
                    }
                }
            }.also { it.isDaemon = true }.start()

            Thread.sleep(500)

            client.subscribeWith().topicFilter("casa/sensor/temperatura").send()
            client.subscribeWith().topicFilter("casa/sensor/luz").send()
            client.subscribeWith().topicFilter("casa/sensor/presenca/sala").send()
            client.subscribeWith().topicFilter("casa/sensor/presenca/cozinha").send()
            println("✅ Subscriptions registradas!")

        } catch (e: Exception) {
            println("❌ Erro MQTT: ${e.message}")
            e.printStackTrace()
        }
    }
}
