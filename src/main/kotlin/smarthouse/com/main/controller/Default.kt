package smarthouse.com.main.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Default {

    @GetMapping("/")
    open fun index(): String {
        return "Running...";
    }
}