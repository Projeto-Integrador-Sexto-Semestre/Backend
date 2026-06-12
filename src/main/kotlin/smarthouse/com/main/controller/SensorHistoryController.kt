package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.CreateSensorHistoryRequest
import smarthouse.com.main.model.SensorHistory
import smarthouse.com.main.repository.SensorHistoryRepository
import smarthouse.com.main.repository.SensorRepository

@RestController
@RequestMapping("/sensor-history")
class SensorHistoryController(
    val repository: SensorHistoryRepository,

    val sensorRepository: SensorRepository
) {
    @GetMapping("/sensor/{sensorId}")
    fun getHistory(@PathVariable sensorId: Long) =

        repository.findBySensorIdOrderByTimestampDesc(sensorId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateSensorHistoryRequest): SensorHistory {

        val sensor = sensorRepository.findById(request.sensorId)

            .orElseThrow { RuntimeException("Sensor id=${request.sensorId} não encontrado") }

        return repository.save(SensorHistory(value = request.value, sensor = sensor))
    }
}