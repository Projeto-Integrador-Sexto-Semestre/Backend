package smarthouse.com.main.controller

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.User
import smarthouse.com.main.repository.UserRepository

@RestController
@RequestMapping("/users")
class UserController(
    val repository: UserRepository,
    val encoder: BCryptPasswordEncoder // Injetado via SecurityConfig
) {

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
            user // Retorna o objeto usuário completo (ou uma mensagem de sucesso)
        } else {
            mapOf("message" to "Senha incorreta")
        }
    }
}