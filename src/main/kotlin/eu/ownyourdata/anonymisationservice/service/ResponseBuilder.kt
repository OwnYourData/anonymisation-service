package eu.ownyourdata.anonymisationservice.service

import jakarta.json.Json
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun createErrorResponse(errorMessage: String): ResponseEntity<String> {
    val builder = Json.createObjectBuilder()
    builder.add("version", Anonymisation.VERSION)
    builder.add("valid", false)
    builder.add("erroreMessage", errorMessage)
    return ResponseEntity(
        builder.build().toString(),
        HttpStatus.BAD_GATEWAY
    )
}

fun createValidResponse(data: List<Map<String, Any>>): ResponseEntity<String> {
    val builder = Json.createObjectBuilder()
    builder.add("version", Anonymisation.VERSION)
    builder.add("valid", true)
    val arrayBuilder = Json.createArrayBuilder()
    data.forEach { instance ->
        val jsonObject = Json.createObjectBuilder()
        instance.entries.forEach{ attribute ->
            jsonObject.add(attribute.key, attribute.value.toString())
        }
        arrayBuilder.add(jsonObject)
    }
    return ResponseEntity(
        builder.build().toString(),
        HttpStatus.ACCEPTED
    )
}