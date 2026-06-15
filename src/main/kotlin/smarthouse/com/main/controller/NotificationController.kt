package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.CreateNotificationRequest
import smarthouse.com.main.model.Notification
import smarthouse.com.main.repository.NotificationRepository
import smarthouse.com.main.repository.UserRepository

@RestController
@RequestMapping("/notifications")
class NotificationController(
    val repository: NotificationRepository,
    val userRepository: UserRepository
) {
    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateNotificationRequest): Notification {
        val user = userRepository.findById(request.userId)
            .orElseThrow { RuntimeException("User id=${request.userId} não encontrado") }
        return repository.save(Notification(message = request.message, user = user))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CreateNotificationRequest): Notification {
        val notification = repository.findById(id).orElseThrow { RuntimeException("Notification id=$id não encontrada") }
        val user = userRepository.findById(request.userId)
            .orElseThrow { RuntimeException("User id=${request.userId} não encontrado") }

        notification.message = request.message
        notification.user = user
        return repository.save(notification)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}