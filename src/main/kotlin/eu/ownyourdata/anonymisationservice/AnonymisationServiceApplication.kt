package eu.ownyourdata.anonymisationservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AnonymisationServiceApplication

fun main(args: Array<String>) {
    runApplication<AnonymisationServiceApplication>(*args)
}
