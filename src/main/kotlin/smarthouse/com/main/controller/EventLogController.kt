package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.EventLog
import smarthouse.com.main.repository.EventLogRepository

@RestController
@RequestMapping("/event-logs")
class EventLogController(val repository: EventLogRepository) {

    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    fun create(@RequestBody eventLog: EventLog) = repository.save(eventLog)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}

