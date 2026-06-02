package smarthouse.com.main.dto

data class RegisterUserRequest(
    val email: String,
    val password: String,
    val name: String,
    val profileId: Long
)