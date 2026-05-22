package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.AutomationRule
import smarthouse.com.main.repository.AutomationRuleRepository

@RestController
@RequestMapping("/automation-rules")
class AutomationRuleController(val repository: AutomationRuleRepository) {

    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    fun create(@RequestBody rule: AutomationRule) = repository.save(rule)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody rule: AutomationRule): AutomationRule {
        val existing = repository.findById(id).orElseThrow()
        val updated = AutomationRule(
            id = existing.id,
            name = rule.name,
            condition = rule.condition,
            enabled = rule.enabled,
            action = rule.action
        )
        return repository.save(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}

