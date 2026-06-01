package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.CreateEventLogRequest
import smarthouse.com.main.model.EventLog
import smarthouse.com.main.repository.EventLogRepository
import smarthouse.com.main.repository.UserRepository

@RestController
@RequestMapping("/event-logs")
class EventLogController(
    val repository: EventLogRepository,
    val userRepository: UserRepository
) {
    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateEventLogRequest): EventLog {
        val user = userRepository.findById(request.userId)
            .orElseThrow { RuntimeException("User id=${request.userId} não encontrado") }
        return repository.save(EventLog(eventType = request.eventType, message = request.message, user = user))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}