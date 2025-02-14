package eu.ownyourdata.anonymisationservice.service

import eu.ownyourdata.anonymisationservice.anonymiser.Anonymiser
import eu.ownyourdata.anonymisationservice.anonymiser.anonymizerFactory
import eu.ownyourdata.anonymisationservice.dto.DatatypeDTO
import eu.ownyourdata.anonymisationservice.dto.RequestDTO
import org.springframework.http.ResponseEntity
import java.lang.IllegalArgumentException
import java.util.HashMap


/**TODO: next steps
 * Anonymization validation based on k-Anonymity and l-Diversity (for Generalization)
 * Tests (same Testing concept as for Reasoning Service
 * Documentation, focus on anonymization methods
 */

fun anonymise(body: RequestDTO): ResponseEntity<String> {
    return try {
        validateConfig(getOntology(body.ontology), body.configuration)
        val anonymisation = Anonymisation(
            body.configuration,
            body.data
        )
        createValidResponse(anonymisation.applyAnonymistation())
    } catch (e: Exception) {
        createErrorResponse(e.localizedMessage)
    }
}

class Anonymisation(configuration: Map<String, DatatypeDTO>, val data: List<Map<String, Any>>) {

    var anonymizer: Map<String, Anonymiser>

    companion object {
        const val SOYA_URL = "https://soya.ownyourdata.eu/OntologyEEG/soya"
        const val ONTOLOGY_URL = "https://soya.ownyourdata.eu/OntologyEEG"
        const val VERSION = "1.0.0"
    }

    init {
        anonymizer = initAnonymizer(configuration)
    }

    fun applyAnonymistation(): List<Map<String, Any>> {
        val valuesPerAttribute = createValuesPerAttribute()
        val anonymisedValues = applyAnonymizater(anonymizer, valuesPerAttribute)
        return createValuesPerInstance(anonymisedValues)
    }

    private fun initAnonymizer(configuration: Map<String, DatatypeDTO>): Map<String, Anonymiser> {
        val anonymiser = HashMap<String, Anonymiser>()
        configuration.entries.forEach { e ->
            anonymiser[e.key] = anonymizerFactory(e.value.anonymisationType, e.value.dataType)
        }
        return anonymiser
    }

    private fun createValuesPerAttribute(): Map<String, MutableList<Any>> {
        val valuesPerAttribute = HashMap<String, MutableList<Any>>()
        this.data.forEach { datapoint -> datapoint.entries.forEach { e ->
            if(valuesPerAttribute.containsKey(e.key)){
                valuesPerAttribute[e.key]?.add(e.value)
            }else{
                valuesPerAttribute[e.key] = mutableListOf(e.value)
            }
        }}
        return valuesPerAttribute
    }

    private fun createValuesPerInstance(valuesPerAttribute: Map<String, List<Any>>): List<Map<String, Any>> {
        val instances = ArrayList<MutableMap<String, Any>>()
        for (i in 0 until valuesPerAttribute.values.first().size) {
            instances.add(i, HashMap<String, Any>())
        }
        valuesPerAttribute.entries.forEach { attribute ->
            for(i in 0 until attribute.value.size) {
                instances[i][attribute.key] = attribute.value[i]
            }
        }
        return instances
    }

    private fun applyAnonymizater(anonymiser: Map<String, Anonymiser>,
                                  valuesPerAttribute: Map<String, MutableList<Any>>): Map<String, List<Any>> {
        val anonymisedValues = HashMap<String, List<Any>>()
        valuesPerAttribute.entries.forEach { e ->
            val anonymiserInstance = anonymiser[e.key]
            if (anonymiserInstance != null) {
                anonymisedValues[e.key] = anonymiserInstance.anonymise(e.value)
            } else {
                throw IllegalArgumentException("For the attribute ${e.key} no anonymiser is defined.")
            }
        }
        return anonymisedValues
    }
}