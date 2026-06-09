package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.LoginRequest
import smarthouse.com.main.dto.RegisterUserRequest
import smarthouse.com.main.dto.UserResponse
import smarthouse.com.main.dto.ProfileResponse
import smarthouse.com.main.model.User
import smarthouse.com.main.repository.ProfileRepository
import smarthouse.com.main.repository.UserRepository
import smarthouse.com.main.security.JwtService

@RestController
@RequestMapping("/users")
class UserController(
    val repository: UserRepository,
    val profileRepository: ProfileRepository,
    val encoder: BCryptPasswordEncoder,
    val authenticationManager: AuthenticationManager,
    val jwtService: JwtService
) {

    @GetMapping
    fun listAll(): List<User> = repository.findAll()

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
            password = encoder.encode(request.password)!!,
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
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, String>> {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )
        val token = jwtService.generateToken(request.email)
        return ResponseEntity.ok(mapOf("token" to token))
    }
}