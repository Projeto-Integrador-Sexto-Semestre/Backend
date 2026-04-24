package smarthouse.com.main.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import smarthouse.com.main.model.SensorHistory

@Repository
interface SensorHistoryRepository : JpaRepository<SensorHistory, Long> {
    fun findBySensorIdOrderByTimestampDesc(sensorId: Long): List<SensorHistory>
}