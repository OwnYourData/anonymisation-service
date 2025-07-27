package eu.ownyourdata.anonymisationservice.service

import com.fasterxml.jackson.databind.JsonNode
import eu.ownyourdata.anonymisationservice.anonymiser.Anonymiser
import eu.ownyourdata.anonymisationservice.anonymiser.Generalization
import eu.ownyourdata.anonymisationservice.anonymiser.anonymizerFactory
import eu.ownyourdata.anonymisationservice.dto.AnonymizationType
import eu.ownyourdata.anonymisationservice.dto.Configuration
import eu.ownyourdata.anonymisationservice.dto.RequestDTO
import jakarta.json.Json
import jakarta.json.JsonValue
import org.springframework.http.ResponseEntity
import java.lang.IllegalArgumentException
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

fun anonymise(body: RequestDTO): ResponseEntity<String> {

    return try {
        val configObject = ConfigObject(body.configurationURL)
        val anonymisation = Anonymisation(
            configObject,
            body.data
        )
        createValidResponse(anonymisation.applyAnonymistation())
    } catch (e: Exception) {
        createErrorResponse(e.localizedMessage)
    }
}

class Anonymisation(private val configObject: ConfigObject, val data: List<Map<String, Any>>) {

    private var anonymizer: Map<String, Anonymiser>

    companion object {
        const val VERSION = "1.0.0"
    }

    init {
        anonymizer = initAnonymizer()
    }

    fun applyAnonymistation(): List<Map<String, JsonValue>> {
        val verticalSchema: Map<String, MutableList<Any?>> = createValuesPerAttribute()
        val anonymisedValues = applyAnonymizer(verticalSchema)
        return createValuesPerInstance(anonymisedValues)
    }

    private fun initAnonymizer(): Map<String, Anonymiser> {
        val anonymiser = HashMap<String, Anonymiser>()
        this.configObject.configuration.forEach { config ->
            anonymiser[config.attribute] = anonymizerFactory(config, configObject)
        }
        return anonymiser
    }

    private fun createValuesPerAttribute(): Map<String, MutableList<Any?>> {
        val valuesPerAttribute: Map<String, MutableList<Any?>> = this.data.stream()
            .flatMap { instance -> instance.keys.stream() }
            .distinct().collect(Collectors.toMap({ it }, { mutableListOf() }))
        this.data.forEach { datapoint -> valuesPerAttribute.keys.forEach { attribute ->
            valuesPerAttribute[attribute]?.add(datapoint[attribute])
        }}
        return valuesPerAttribute
    }

    private fun createValuesPerInstance(valuesPerAttribute: Map<String, List<JsonValue?>>): List<Map<String, JsonValue>> {
        val instances = ArrayList<MutableMap<String, JsonValue>>()
        for (i in 0 until valuesPerAttribute.values.first().size) {
            instances.add(i, HashMap<String, JsonValue>())
        }
        valuesPerAttribute.entries.forEach { attribute ->
            for(i in 0 until attribute.value.size) {
                if (attribute.value[i] != null) {
                    instances[i][attribute.key] = attribute.value[i] as JsonValue
                }
            }
        }
        return instances
    }

    private fun applyAnonymizer(verticalSchema: Map<String, MutableList<Any?>>): Map<String, List<JsonValue?>> {
        val anonymisedValues = HashMap<String, List<JsonValue?>>()
        val attributeCount = verticalSchema.keys.filter { attribute ->
            val config: Optional<Configuration> = this.configObject.configuration.stream()
                .filter { c -> c.attribute == attribute }
                .findFirst()
            config.isPresent && (
                    config.get().anonaymizationType == AnonymizationType.RANDOMIZATION ||
                    config.get().anonaymizationType == AnonymizationType.GENERALIZATION)
        }.size
        verticalSchema.entries.forEach { e ->
            val anonymiserInstance = anonymizer[e.key]
            if (anonymiserInstance != null) {
                try {
                    val jsonValues: List<JsonValue?> = anonymiserInstance.anonymiseWithNulls(e.value, attributeCount)
                    anonymisedValues[anonymiserInstance.getAttributeName(e.key)] = jsonValues
                } catch (error: Exception) {
                    throw IllegalArgumentException("Error when applying anonymization to attribute ${e.key}: ${error.message}")
                }
            } else {
                anonymisedValues[e.key] = e.value.stream().map { v -> Json.createValue(v.toString()) }.toList()
            }
        }
        return anonymisedValues
    }
}