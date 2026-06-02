package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.RegisterUserRequest
import smarthouse.com.main.dto.UserResponse
import smarthouse.com.main.dto.ProfileResponse
import smarthouse.com.main.model.User
import smarthouse.com.main.repository.ProfileRepository
import smarthouse.com.main.repository.UserRepository

@RestController
@RequestMapping("/users")
class UserController(
    val repository: UserRepository,
    val profileRepository: ProfileRepository,
    val encoder: BCryptPasswordEncoder
) {

    // Lista todos os usuários
    @GetMapping
    fun listAll(): List<User> = repository.findAll()

    // Busca usuário por ID
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): User =
        repository.findById(id).orElseThrow { RuntimeException("Usuário não encontrado") }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: RegisterUserRequest): UserResponse {
        val profile = profileRepository.findById(request.profileId)
            .orElseThrow { RuntimeException("Profile id=${request.profileId} não encontrado") }

        val user = User(
            email    = request.email,
            password = encoder.encode(request.password) ?: throw RuntimeException("Erro ao encriptar senha"),
            name     = request.name,
            profile  = profile
        )

        val saved = repository.save(user)

        return UserResponse(
            id      = saved.id,
            email   = saved.email,
            name    = saved.name,
            profile = ProfileResponse(
                id                = profile.id,
                name              = profile.name,
                canControlDevices = profile.canControlDevices,
                canEditStructure  = profile.canEditStructure,
                canViewLogs       = profile.canViewLogs
            )
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody loginData: Map<String, String>): Any {
        val email = loginData["email"] ?: ""
        val password = loginData["password"] ?: ""

        val user = repository.findByEmail(email)
            ?: return mapOf("message" to "Usuário não encontrado")

        return if (encoder.matches(password, user.password)) {
            UserResponse(
                id      = user.id,
                email   = user.email,
                name    = user.name,
                profile = user.profile?.let {
                    ProfileResponse(
                        id                = it.id,
                        name              = it.name,
                        canControlDevices = it.canControlDevices,
                        canEditStructure  = it.canEditStructure,
                        canViewLogs       = it.canViewLogs
                    )
                }
            )
        } else {
            mapOf("message" to "Senha incorreta")
        }
    }
}