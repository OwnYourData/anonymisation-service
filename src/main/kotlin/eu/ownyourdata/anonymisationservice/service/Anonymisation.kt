package eu.ownyourdata.anonymisationservice.service

import eu.ownyourdata.anonymisationservice.anonymiser.Anonymiser
import eu.ownyourdata.anonymisationservice.anonymiser.anonymizerFactory
import eu.ownyourdata.anonymisationservice.dto.RequestDTO
import org.springframework.http.ResponseEntity
import java.lang.IllegalArgumentException
import java.util.HashMap
import java.util.stream.Collectors

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

    fun applyAnonymistation(): List<Map<String, Any>> {
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

    private fun createValuesPerInstance(valuesPerAttribute: Map<String, List<Any?>>): List<Map<String, Any>> {
        val instances = ArrayList<MutableMap<String, Any>>()
        for (i in 0 until valuesPerAttribute.values.first().size) {
            instances.add(i, HashMap<String, Any>())
        }
        valuesPerAttribute.entries.forEach { attribute ->
            for(i in 0 until attribute.value.size) {
                if (attribute.value[i] != null) {
                    instances[i][attribute.key] = attribute.value[i] as Any
                }
            }
        }
        return instances
    }

    private fun applyAnonymizer(verticalSchema: Map<String, MutableList<Any?>>): Map<String, List<Any?>> {
        val anonymisedValues = HashMap<String, List<Any?>>()
        verticalSchema.entries.forEach { e ->
            val anonymiserInstance = anonymizer[e.key.lowercase()]
            if (anonymiserInstance != null) {
                try {
                    anonymisedValues[e.key] = anonymiserInstance.anonymiseWithNulls(e.value)
                } catch (error: Exception) {
                    throw IllegalArgumentException("Error when applying anonymization to attribute ${e.key}: ${error.message}")
                }
            } else {
                throw IllegalArgumentException("For the attribute \'${e.key}\' no anonymiser is defined.")
            }
        }
        return anonymisedValues
    }
}