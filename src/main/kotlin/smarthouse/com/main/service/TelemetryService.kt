package smarthouse.com.main.service

import org.springframework.stereotype.Service
import smarthouse.com.main.repository.*
import smarthouse.com.main.model.*
import java.time.LocalDateTime

@Service
class TelemetryService(
    private val sensorRepository: SensorRepository,
    private val historyRepository: SensorHistoryRepository,
    private val eventLogRepository: EventLogRepository,
    private val notificationRepository: NotificationRepository
) {

    fun processarMensagem(topico: String, payload: String) {
        val partes = topico.split("/")
        val sensorId = partes.getOrNull(1)?.toLongOrNull() ?: return
        val sensor = sensorRepository.findById(sensorId).orElse(null) ?: return

        val timestamp = LocalDateTime.now()

        // 1. Sempre alimenta o Histórico (o valor bruto da leitura)
        val history = SensorHistory(value = payload, timestamp = timestamp, sensor = sensor)
        historyRepository.save(history)

        // 2. Sempre alimenta o Log de Eventos (registro de atividade)
        val log = EventLog(
            eventType = "MENSAGEM_MQTT",
            message = "Sensor ${sensor.name} enviou: $payload no tópico $topico",
            timestamp = timestamp,
            user = sensor.room?.house?.user
        )
        eventLogRepository.save(log)

        // 3. Sempre alimenta a Notificação (alerta para o sistema/usuário)
        val notificacao = Notification(
            message = "Atualização de ${sensor.name}: $payload",
            timestamp = timestamp,
            read = false,
            user = sensor.room?.house?.user
        )
        notificationRepository.save(notificacao)
    }
}