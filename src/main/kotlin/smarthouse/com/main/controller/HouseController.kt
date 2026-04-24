package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.House
import smarthouse.com.main.repository.HouseRepository

@RestController
@RequestMapping("/houses")
class HouseController(val repository: HouseRepository) {

    @GetMapping
    fun listAll(): List<House> = repository.findAll()

    @PostMapping
    fun create(@RequestBody house: House): House {
        return repository.save(house)
    }

    @GetMapping("/user/{userId}")
    fun listByUser(@PathVariable userId: Long): List<House> {
        return repository.findByUserId(userId)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        repository.deleteById(id)
    }
}