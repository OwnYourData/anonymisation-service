package eu.ownyourdata.anonymisationservice

import eu.ownyourdata.anonymisationservice.dto.RequestDTO
import org.springframework.web.bind.annotation.GetMapping
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import eu.ownyourdata.anonymisationservice.service.anonymise
import org.springframework.web.bind.annotation.RequestBody


@RestController
class AnonymisationServiceController {

    companion object {
        const val VERSION = "1.0.0"
    }

    @GetMapping("/")
    fun status(): String {
        return buildJsonObject{
            put("service", "Anonymisation")
            put("version", VERSION)
        }.toString()
    }

    @PutMapping("/api/anonymise")
    fun anonymiseRequest(@RequestBody body: RequestDTO): String {
        return anonymise(body)
    }

}