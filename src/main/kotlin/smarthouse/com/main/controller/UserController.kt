package smarthouse.com.main.controller

import smarthouse.com.main.model.User
import smarthouse.com.main.repository.UserRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(val repository: UserRepository) {

    @GetMapping
    fun list(): List<User> = repository.findAll()

    @PostMapping
    fun create(@RequestBody user: User): User = repository.save(user)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}