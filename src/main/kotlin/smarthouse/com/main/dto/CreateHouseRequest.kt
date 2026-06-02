package smarthouse.com.main.dto

data class CreateHouseRequest(
    val name: String,
    val address: String,
    val userId: Long
)