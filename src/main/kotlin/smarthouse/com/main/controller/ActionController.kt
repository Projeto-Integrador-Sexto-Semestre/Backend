package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.CreateActionRequest
import smarthouse.com.main.model.Action
import smarthouse.com.main.repository.ActionRepository
import smarthouse.com.main.repository.IotDeviceRepository

@RestController
@RequestMapping("/actions")
class ActionController(
    val repository: ActionRepository,
    val deviceRepository: IotDeviceRepository
) {
    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateActionRequest): Action {
        val device = deviceRepository.findById(request.deviceId)
            .orElseThrow { RuntimeException("Device id=${request.deviceId} não encontrado") }
        return repository.save(Action(name = request.name, device = device, command = request.command))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}