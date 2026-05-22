package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.Notification
import smarthouse.com.main.repository.NotificationRepository

@RestController
@RequestMapping("/notifications")
class NotificationController(val repository: NotificationRepository) {

    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    fun create(@RequestBody notification: Notification) = repository.save(notification)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody notification: Notification): Notification {
        val existing = repository.findById(id).orElseThrow()
        val updated = Notification(
            id = existing.id,
            message = notification.message,
            timestamp = notification.timestamp,
            read = notification.read,
            user = notification.user
        )
        return repository.save(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}

