package eu.ownyourdata.anonymisationservice

import eu.ownyourdata.anonymisationservice.dto.RequestDTO
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import eu.ownyourdata.anonymisationservice.service.anonymise
import eu.ownyourdata.anonymisationservice.service.createErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Paths


@RestController
class AnonymisationServiceController {

    companion object {
        const val VERSION = "1.0.0"
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidJson(exception: HttpMessageNotReadableException): ResponseEntity<String> {
        return createErrorResponse(exception.localizedMessage)
    }

    @ApiResponses(
        ApiResponse(responseCode = "202", description = "Accepted", content = [Content(mediaType = "application/json")])
    )
    @Operation(
        summary = "Version",
        description = "Returns the current Version of the Anoymizer application"
    )
    @GetMapping("/")
    fun status(): String {
        return buildJsonObject{
            put("service", "Anonymisation")
            put("version", VERSION)
        }.toString()
    }

    @ApiResponses(
        ApiResponse(responseCode = "202", description = "Accepted", content = [Content(mediaType = "application/json")]),
        ApiResponse(responseCode = "400", description = "Error", content = [Content(mediaType = "application/json")])
    )
    @Operation(
        summary = "Apply Anonymization",
        description = "Anonymizing the input data based on the configuration provided via URL"
    )
    @PutMapping("/api/anonymise")
    fun anonymiseRequest(@RequestBody body: RequestDTO): ResponseEntity<String> {
        return anonymise(body)
    }

}