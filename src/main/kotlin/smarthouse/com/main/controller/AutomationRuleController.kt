package smarthouse.com.main.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import smarthouse.com.main.dto.CreateAutomationRuleRequest
import smarthouse.com.main.model.AutomationRule
import smarthouse.com.main.repository.ActionRepository
import smarthouse.com.main.repository.AutomationRuleRepository

@RestController
@RequestMapping("/automation-rules")
class AutomationRuleController(
    val repository: AutomationRuleRepository,
    val actionRepository: ActionRepository
) {
    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateAutomationRuleRequest): AutomationRule {
        val action = actionRepository.findById(request.actionId)
            .orElseThrow { RuntimeException("Action id=${request.actionId} não encontrada") }
        return repository.save(AutomationRule(name = request.name, condition = request.condition, enabled = request.enabled, action = action))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CreateAutomationRuleRequest): AutomationRule {
        val rule = repository.findById(id).orElseThrow { RuntimeException("AutomationRule id=$id não encontrada") }
        val action = actionRepository.findById(request.actionId)
            .orElseThrow { RuntimeException("Action id=${request.actionId} não encontrada") }

        rule.name = request.name
        rule.condition = request.condition
        rule.enabled = request.enabled
        rule.action = action
        return repository.save(rule)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}