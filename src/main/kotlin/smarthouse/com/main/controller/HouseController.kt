package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.CreateHouseRequest
import smarthouse.com.main.model.House
import smarthouse.com.main.repository.HouseRepository
import smarthouse.com.main.repository.UserRepository

@RestController
@RequestMapping("/houses")
class HouseController(
    val repository: HouseRepository,
    val userRepository: UserRepository
) {

    @GetMapping
    fun listAll(): List<House> = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateHouseRequest): House {
        val user = userRepository.findById(request.userId)
            .orElseThrow { RuntimeException("User id=${request.userId} não encontrado") }

        val house = House(
            name    = request.name,
            address = request.address,
            user    = user
        )

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