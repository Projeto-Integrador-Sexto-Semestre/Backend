package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.IotDevice
import smarthouse.com.main.repository.IotDeviceRepository

@RestController
@RequestMapping("/devices")
class IotDeviceController(val repository: IotDeviceRepository) {

    @GetMapping
    fun listAll(): List<IotDevice> = repository.findAll()

    @PostMapping
    fun create(@RequestBody device: IotDevice): IotDevice {
        return repository.save(device)
    }

    @GetMapping("/room/{roomId}")
    fun listByRoom(@PathVariable roomId: Long) = repository.findByRoomId(roomId)

    // Rota rápida para ligar/desligar
    @PatchMapping("/{id}/status")
    fun updateStatus(@PathVariable id: Long, @RequestBody body: Map<String, String>): IotDevice {
        val device = repository.findById(id).orElseThrow { RuntimeException("Dispositivo não encontrado") }
        device.status = body["status"] ?: device.status
        return repository.save(device)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}