package smarthouse.com.main.dto

data class CreateNotificationRequest(
    val message: String,
    val userId: Long
)