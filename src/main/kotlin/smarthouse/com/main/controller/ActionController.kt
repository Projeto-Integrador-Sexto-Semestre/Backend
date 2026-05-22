package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.Action
import smarthouse.com.main.repository.ActionRepository

@RestController
@RequestMapping("/actions")
class ActionController(val repository: ActionRepository) {

    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    fun create(@RequestBody action: Action) = repository.save(action)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody action: Action): Action {
        val existing = repository.findById(id).orElseThrow()
        val updated = Action(
            id = existing.id,
            name = action.name,
            device = action.device,
            command = action.command
        )
        return repository.save(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}

