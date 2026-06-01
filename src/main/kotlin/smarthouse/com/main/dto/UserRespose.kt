package smarthouse.com.main.dto

data class UserResponse(
    val id: Long?,
    val email: String,
    val name: String,
    val profile: ProfileResponse?
)

data class ProfileResponse(
    val id: Long?,
    val name: String,
    val canControlDevices: Boolean?,
    val canEditStructure: Boolean?,
    val canViewLogs: Boolean?
)