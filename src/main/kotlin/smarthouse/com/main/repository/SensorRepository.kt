package smarthouse.com.main.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import smarthouse.com.main.model.Sensor

@Repository
interface SensorRepository : JpaRepository<Sensor, Long> {
    fun findByMqttTopic(mqttTopic: String): Sensor?
}