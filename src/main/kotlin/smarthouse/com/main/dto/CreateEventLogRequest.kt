package smarthouse.com.main.dto

data class CreateEventLogRequest(
    val eventType: String,
    val message: String,
    val userId: Long
)