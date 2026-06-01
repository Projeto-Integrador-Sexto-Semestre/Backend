package smarthouse.com.main.dto

data class CreateIotDeviceRequest(
    val name: String,
    val deviceTypeId: Long,
    val topic: String,
    val status: String = "OFF",
    val roomId: Long
)