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

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody type: AlertType): AlertType {
        val existing = repository.findById(id).orElseThrow { RuntimeException("AlertType id=$id não encontrado") }
        existing.name = type.name // Assumindo propriedade name
        return repository.save(existing)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}