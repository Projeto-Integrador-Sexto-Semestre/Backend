package smarthouse.com.main.dto

data class CreateAlertRequest(
    val message: String,
    val alertTypeId: Long,
    val sensorId: Long? = null,
    val deviceId: Long? = null
)