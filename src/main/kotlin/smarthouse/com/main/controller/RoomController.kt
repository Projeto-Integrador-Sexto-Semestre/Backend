package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.Room
import smarthouse.com.main.repository.RoomRepository

@RestController
@RequestMapping("/rooms")
class RoomController(val repository: RoomRepository) {

    @GetMapping
    fun listAll(): List<Room> = repository.findAll()

    @PostMapping
    fun create(@RequestBody room: Room): Room {
        return repository.save(room)
    }

    @GetMapping("/house/{houseId}")
    fun listByHouse(@PathVariable houseId: Long): List<Room> {
        return repository.findByHouseId(houseId)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        repository.deleteById(id)
    }
}