package eu.ownyourdata.anonymisationservice.service

import eu.ownyourdata.anonymisationservice.anonymiser.Anonymiser
import eu.ownyourdata.anonymisationservice.anonymiser.anonymizerFactory
import eu.ownyourdata.anonymisationservice.dto.DatatypeDTO
import eu.ownyourdata.anonymisationservice.dto.RequestDTO
import org.apache.commons.lang3.ObjectUtils.Null
import org.apache.jena.ontology.OntModel
import org.springframework.http.ResponseEntity
import java.util.HashMap
import java.util.LinkedList


/*
TODO: next steps
* Anonymizer methods
* Reasoning
 */

fun anonymise(body: RequestDTO): ResponseEntity<String> {
    try {
        validateConfig(getOntology(body.ontology), body.configuration)
        val anonymisation = Anonymisation(
            body.configuration,
            body.data
        )
        return createValidResponse(anonymisation.applyAnonymistation())
    } catch (e: Exception) {
        return createErrorResponse(e.localizedMessage)
    }

}

class Anonymisation(configuration: Map<String, DatatypeDTO>, val data: List<Map<String, Any>>) {

    var anonymizer: Map<String, Anonymiser>

    companion object {
        const val SOYA_URL = "https://soya.ownyourdata.eu/OntologyEEG/soya"
        const val ONTOLOGY_URL = "https://soya.ownyourdata.eu/OntologyEEG/"
        const val VERSION = "1.0.0"
    }

    init {
        anonymizer = initAnonymizer(configuration)
    }

    fun applyAnonymistation(): List<Map<String, Any>> {
        val valuesPerAttribute = createValuesPerAttribute()
        val anonymisedValues = applyAnonymizater(anonymizer, valuesPerAttribute)
        return LinkedList()
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
        data.forEach { datapoint -> datapoint.entries.forEach { e ->
            if(valuesPerAttribute.containsKey(e.key)){
                valuesPerAttribute[e.key]?.add(e.value)
            }else{
                valuesPerAttribute[e.key] = mutableListOf(e.value)
            }
        }}
        return valuesPerAttribute
    }

    private fun applyAnonymizater(anonymiser: Map<String, Anonymiser>,
                                  valuesPerAttribute: Map<String, MutableList<Any>>) {
        val anonymisedValues = HashMap<String, List<Any>>()
        valuesPerAttribute.entries.forEach { e ->
            anonymisedValues[e.key] = anonymiser[e.key]!!.anonymise(e.value)
        }
    }
}