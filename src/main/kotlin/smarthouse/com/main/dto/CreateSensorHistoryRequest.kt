package smarthouse.com.main.dto

data class CreateSensorHistoryRequest(
    val value: String,
    val sensorId: Long
)