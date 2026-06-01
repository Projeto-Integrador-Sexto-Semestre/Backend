package smarthouse.com.main

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["smarthouse.com"])
class MainApplication

fun main(args: Array<String>) {
    runApplication<MainApplication>(*args)
}