package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.mqtt.MqttService

@RestController
@RequestMapping("/mqtt")
class MqttController(val mqttService: MqttService) {

    @PostMapping("/ventilador/ligar")
    fun ligarVentilador() = mqttService.ligarVentilador()

    @PostMapping("/ventilador/desligar")
    fun desligarVentilador() = mqttService.desligarVentilador()

    @PostMapping("/luz/ligar")
    fun ligarLuz() = mqttService.ligarLuz()

    @PostMapping("/luz/desligar")
    fun desligarLuz() = mqttService.desligarLuz()

    @PostMapping("/threshold/temp")
    fun setThresholdTemp(@RequestBody body: Map<String, Float>) =
        mqttService.setThresholdTemp(body["valor"] ?: 0f)

    @PostMapping("/threshold/ldr")
    fun setThresholdLdr(@RequestBody body: Map<String, Int>) =
        mqttService.setThresholdLdr(body["valor"] ?: 0)

    @PostMapping("/bloqueio/temp")
    fun bloquearAutoTemp(@RequestBody body: Map<String, Boolean>) =
        mqttService.bloquearAutoTemp(body["bloquear"] ?: false)

    @PostMapping("/bloqueio/luz")
    fun bloquearAutoLuz(@RequestBody body: Map<String, Boolean>) =
        mqttService.bloquearAutoLuz(body["bloquear"] ?: false)
}