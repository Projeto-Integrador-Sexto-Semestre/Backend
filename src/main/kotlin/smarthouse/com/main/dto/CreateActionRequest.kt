package smarthouse.com.main.dto

data class CreateActionRequest(
    val name: String,
    val deviceId: Long,
    val command: String
)