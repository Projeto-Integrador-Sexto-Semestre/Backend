package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.Sensor
import smarthouse.com.main.repository.SensorRepository

@RestController
@RequestMapping("/sensors")
class SensorController(val repository: SensorRepository) {

    @GetMapping
    fun listAll() = repository.findAll()

    @PostMapping
    fun create(@RequestBody sensor: Sensor) = repository.save(sensor)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}