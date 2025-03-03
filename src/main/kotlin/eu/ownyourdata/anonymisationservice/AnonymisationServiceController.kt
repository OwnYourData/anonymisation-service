package eu.ownyourdata.anonymisationservice

import eu.ownyourdata.anonymisationservice.dto.RequestDTO
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import eu.ownyourdata.anonymisationservice.service.anonymise
import eu.ownyourdata.anonymisationservice.service.createErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*


@RestController
class AnonymisationServiceController {

    companion object {
        const val VERSION = "1.0.0"
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidJson(exception: HttpMessageNotReadableException): ResponseEntity<String> {
        return createErrorResponse(exception.localizedMessage)
    }

    @GetMapping("/")
    fun status(): String {
        return buildJsonObject{
            put("service", "Anonymisation")
            put("version", VERSION)
        }.toString()
    }

    @PutMapping("/api/anonymise")
    fun anonymiseRequest(@RequestBody body: RequestDTO): ResponseEntity<String> {
        return anonymise(body)
    }

}