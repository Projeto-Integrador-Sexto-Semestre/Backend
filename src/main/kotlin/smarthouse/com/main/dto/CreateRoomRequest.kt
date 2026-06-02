package smarthouse.com.main.dto

data class CreateRoomRequest(
    val name: String,
    val type: String,
    val houseId: Long
)