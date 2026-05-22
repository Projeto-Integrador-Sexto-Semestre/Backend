package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.AlertType
import smarthouse.com.main.repository.AlertTypeRepository

@RestController
@RequestMapping("/alert-types")
class AlertTypeController(val repository: AlertTypeRepository) {

    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    fun create(@RequestBody type: AlertType) = repository.save(type)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}

