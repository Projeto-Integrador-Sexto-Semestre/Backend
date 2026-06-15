package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.CreateIotDeviceRequest
import smarthouse.com.main.model.IotDevice
import smarthouse.com.main.repository.DeviceTypeRepository
import smarthouse.com.main.repository.IotDeviceRepository
import smarthouse.com.main.repository.RoomRepository

@RestController
@RequestMapping("/devices")
class IotDeviceController(
    val repository: IotDeviceRepository,
    val deviceTypeRepository: DeviceTypeRepository,
    val roomRepository: RoomRepository
) {
    @GetMapping
    fun listAll(): List<IotDevice> = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateIotDeviceRequest): IotDevice {
        val deviceType = deviceTypeRepository.findById(request.deviceTypeId)
            .orElseThrow { RuntimeException("DeviceType id=${request.deviceTypeId} não encontrado") }
        val room = roomRepository.findById(request.roomId)
            .orElseThrow { RuntimeException("Room id=${request.roomId} não encontrada") }
        return repository.save(IotDevice(name = request.name, deviceType = deviceType, topic = request.topic, status = request.status, room = room))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CreateIotDeviceRequest): IotDevice {
        val device = repository.findById(id).orElseThrow { RuntimeException("Device id=$id não encontrado") }
        val deviceType = deviceTypeRepository.findById(request.deviceTypeId)
            .orElseThrow { RuntimeException("DeviceType id=${request.deviceTypeId} não encontrado") }
        val room = roomRepository.findById(request.roomId)
            .orElseThrow { RuntimeException("Room id=${request.roomId} não encontrada") }

        device.name = request.name
        device.deviceType = deviceType
        device.topic = request.topic
        device.status = request.status
        device.room = room
        return repository.save(device)
    }

    @GetMapping("/room/{roomId}")
    fun listByRoom(@PathVariable roomId: Long) = repository.findByRoomId(roomId)

    @PatchMapping("/{id}/status")
    fun updateStatus(@PathVariable id: Long, @RequestBody body: Map<String, String>): IotDevice {
        val device = repository.findById(id).orElseThrow { RuntimeException("Dispositivo não encontrado") }
        device.status = body["status"] ?: device.status
        return repository.save(device)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}