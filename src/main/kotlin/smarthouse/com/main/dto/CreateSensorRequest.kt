package smarthouse.com.main.dto

data class CreateSensorRequest(
    val name: String,
    val mqttTopic: String,
    val deviceTypeId: Long,
    val roomId: Long
)