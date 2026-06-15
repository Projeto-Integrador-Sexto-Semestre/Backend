package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.*
import smarthouse.com.main.model.Profile
import smarthouse.com.main.repository.ProfileRepository

@RestController
@RequestMapping("/profiles")
class ProfileController(val repository: ProfileRepository) {

    @GetMapping
    fun list() = repository.findAll()

    @PostMapping
    fun create(@RequestBody profile: Profile) = repository.save(profile)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody profile: Profile): Profile {

        val existing = repository.findById(id).orElseThrow()

        val updated = Profile(
            id = existing.id,
            name = profile.name,
            description = profile.description,
            canControlDevices = profile.canControlDevices,
            canEditStructure = profile.canEditStructure,
            canViewLogs = profile.canViewLogs
        )

        return repository.save(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = repository.deleteById(id)
}