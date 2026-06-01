package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.CreateAlertRequest
import smarthouse.com.main.model.Alert
import smarthouse.com.main.repository.AlertRepository
import smarthouse.com.main.repository.AlertTypeRepository
import smarthouse.com.main.repository.IotDeviceRepository
import smarthouse.com.main.repository.SensorRepository

@RestController
@RequestMapping("/alerts")
class AlertController(
    val repository: AlertRepository,
    val alertTypeRepository: AlertTypeRepository,
    val sensorRepository: SensorRepository,
    val deviceRepository: IotDeviceRepository
) {
    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateAlertRequest): Alert {
        val alertType = alertTypeRepository.findById(request.alertTypeId)
            .orElseThrow { RuntimeException("AlertType id=${request.alertTypeId} não encontrado") }
        val sensor = request.sensorId?.let { sensorRepository.findById(it).orElse(null) }
        val device = request.deviceId?.let { deviceRepository.findById(it).orElse(null) }
        return repository.save(Alert(message = request.message, alertType = alertType, sensor = sensor, device = device))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}