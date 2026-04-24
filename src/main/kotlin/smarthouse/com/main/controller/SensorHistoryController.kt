package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.SensorHistory
import smarthouse.com.main.repository.SensorHistoryRepository

@RestController
@RequestMapping("/sensor-history")
class SensorHistoryController(val repository: SensorHistoryRepository) {

    @GetMapping("/sensor/{sensorId}")
    fun getHistory(@PathVariable sensorId: Long) =
        repository.findBySensorIdOrderByTimestampDesc(sensorId)

    @PostMapping
    fun saveLeitura(@RequestBody history: SensorHistory) = repository.save(history)
}