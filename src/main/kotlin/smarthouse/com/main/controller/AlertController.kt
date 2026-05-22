package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.Alert
import smarthouse.com.main.repository.AlertRepository

@RestController
@RequestMapping("/alerts")
class AlertController(val repository: AlertRepository) {

    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    fun create(@RequestBody alert: Alert) = repository.save(alert)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody alert: Alert): Alert {
        val existing = repository.findById(id).orElseThrow()
        val updated = Alert(
            id = existing.id,
            message = alert.message,
            timestamp = alert.timestamp,
            acknowledged = alert.acknowledged,
            alertType = alert.alertType,
            sensor = alert.sensor,
            device = alert.device
        )
        return repository.save(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}

