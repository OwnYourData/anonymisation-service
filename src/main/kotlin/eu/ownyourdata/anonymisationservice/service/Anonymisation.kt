package eu.ownyourdata.anonymisationservice.service

import eu.ownyourdata.anonymisationservice.anonymiser.Anonymiser
import eu.ownyourdata.anonymisationservice.anonymiser.anonymizerFactory
import eu.ownyourdata.anonymisationservice.dto.DatatypeDTO
import eu.ownyourdata.anonymisationservice.dto.RequestDTO
import org.apache.jena.ontology.OntModel
import java.util.HashMap


fun anonymise(body: RequestDTO) : String {
    // TODO define the logic here
    var anonymisation = Anonymisation(
        getOntology(body.ontology),
        body.configuration,
        body.data
    )
    // for each attribute check if the anonymization in the config is valid
    // for each attribute create a anonymiser
    // apply the anonymisation with the anonymiser
    // build a response json
    return ""
}

class Anonymisation(val ontology: OntModel, configuration: Map<String, DatatypeDTO>, val data: List<Map<String, Any>>) {

    companion object {
        const val SOYA_URL = "https://soya.ownyourdata.eu/OntologyEEG/soya"
        const val ONTOLOGY_URL = "https://soya.ownyourdata.eu/OntologyEEG/"
    }

    init {
        val invalidAttributes = validateConfig(ontology, configuration)
        val anonymizer = initAnonymizer(configuration)
    }

    fun initAnonymizer(configuration: Map<String, DatatypeDTO>): Map<String, Anonymiser<*>> {
        val anonymiser = HashMap<String, Anonymiser<*>>()
        configuration.entries.forEach { e -> anonymiser.put(e.key,
            anonymizerFactory(e.value.anonymisationType, e.value.dataType)) }
        return anonymiser
    }
}