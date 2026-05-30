package smarthouse.com.main.controller

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.User
import smarthouse.com.main.repository.UserRepository

@RestController
@RequestMapping("/users")
class UserController(
    val repository: UserRepository,
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
    fun register(@RequestBody user: User): User {
        user.password = encoder.encode(user.password)!!
        return repository.save(user)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginData: Map<String, String>): Any {
        val email = loginData["email"] ?: ""
        val password = loginData["password"] ?: ""

        val user = repository.findByEmail(email)
            ?: return mapOf("message" to "Usuário não encontrado")

        return if (encoder.matches(password, user.password)) {
            user
        } else {
            mapOf("message" to "Senha incorreta")
        }
    }
}