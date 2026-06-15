package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.DeviceType
import smarthouse.com.main.repository.DeviceTypeRepository

@RestController
@RequestMapping("/device-types")
class DeviceTypeController(val repository: DeviceTypeRepository) {

    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    fun create(@RequestBody type: DeviceType) = repository.save(type)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody type: DeviceType): DeviceType {
        val existing = repository.findById(id).orElseThrow { RuntimeException("DeviceType id=$id não encontrado") }
        existing.name = type.name
        return repository.save(existing)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}