package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.CreateSensorRequest
import smarthouse.com.main.model.Sensor
import smarthouse.com.main.repository.DeviceTypeRepository
import smarthouse.com.main.repository.RoomRepository
import smarthouse.com.main.repository.SensorRepository

@RestController
@RequestMapping("/sensors")
class SensorController(
    val repository: SensorRepository,
    val deviceTypeRepository: DeviceTypeRepository,
    val roomRepository: RoomRepository
) {
    @GetMapping
    fun listAll() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateSensorRequest): Sensor {
        val deviceType = deviceTypeRepository.findById(request.deviceTypeId)
            .orElseThrow { RuntimeException("DeviceType id=${request.deviceTypeId} não encontrado") }
        val room = roomRepository.findById(request.roomId)
            .orElseThrow { RuntimeException("Room id=${request.roomId} não encontrada") }
        return repository.save(Sensor(name = request.name, mqttTopic = request.mqttTopic, deviceType = deviceType, room = room))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CreateSensorRequest): Sensor {
        val sensor = repository.findById(id).orElseThrow { RuntimeException("Sensor id=$id não encontrado") }
        val deviceType = deviceTypeRepository.findById(request.deviceTypeId)
            .orElseThrow { RuntimeException("DeviceType id=${request.deviceTypeId} não encontrado") }
        val room = roomRepository.findById(request.roomId)
            .orElseThrow { RuntimeException("Room id=${request.roomId} não encontrada") }

        sensor.name = request.name
        sensor.mqttTopic = request.mqttTopic
        sensor.deviceType = deviceType
        sensor.room = room
        return repository.save(sensor)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}