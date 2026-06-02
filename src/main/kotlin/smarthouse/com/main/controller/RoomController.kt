package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.CreateRoomRequest
import smarthouse.com.main.model.Room
import smarthouse.com.main.repository.HouseRepository
import smarthouse.com.main.repository.RoomRepository

@RestController
@RequestMapping("/rooms")
class RoomController(
    val repository: RoomRepository,
    val houseRepository: HouseRepository
) {
    @GetMapping
    fun listAll(): List<Room> = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateRoomRequest): Room {
        val house = houseRepository.findById(request.houseId)
            .orElseThrow { RuntimeException("House id=${request.houseId} não encontrada") }
        return repository.save(Room(name = request.name, type = request.type, house = house))
    }

    @GetMapping("/house/{houseId}")
    fun listByHouse(@PathVariable houseId: Long) = repository.findByHouseId(houseId)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}