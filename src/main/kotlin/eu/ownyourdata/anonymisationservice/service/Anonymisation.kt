package eu.ownyourdata.anonymisationservice.service

import eu.ownyourdata.anonymisationservice.dto.RequestDTO
import org.apache.commons.io.IOUtils
import org.apache.jena.ontology.OntModel
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.*
import org.semanticweb.owlapi.reasoner.OWLReasoner


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

// TODO: state the general anonymisation class here
class Anonymisation(val ontology: OntModel, val configuration: Map<String, String>, val data: List<Map<String, Any>>) {

    companion object {
        const val SOYA_URL = "https://soya.ownyourdata.eu/OntologyEEG/soya#"
        const val ONTOLOGY_URL = "https://soya.ownyourdata.eu/OntologyEEG/"
    }

    init {
        addConfigToOntology()
        validateConfig()
    }

    private fun validateConfig(){
        val manager = OWLManager.createOWLOntologyManager()
        val o = createOWLOntology(manager)
        o.saveOntology(System.out)
        val r = ReasonerFactory().createReasoner(o)
        val invalidAttributes: List<String>  = configuration.keys.stream()
            .filter { attr: String -> attributeComplied(attr, r, manager) }
            .toList()
        if(invalidAttributes.isNotEmpty()) {
          throw IllegalArgumentException(
              "The anonymization operation for ${invalidAttributes.joinToString(", ")} are not allowed")
        }
    }

    // Reasoning is not working as the config classes are interpreted as annotations not as named individuals
    // TODO: make sure that the classes are classes in OWL to enable reasoning
    private fun attributeComplied(attribute: String, r: OWLReasoner, manager: OWLOntologyManager): Boolean {
        val df = manager.owlDataFactory
        val axiom = df.getOWLSubClassOfAxiom(
            df.getOWLClass(IRI.create(SOYA_URL+attribute)),
            df.getOWLClass(IRI.create(ONTOLOGY_URL+attribute)))
        println(r.isEntailed(axiom))
        return !r.isEntailed(axiom)
    }

    private fun createOWLOntology(manager: OWLOntologyManager): OWLOntology {
        val o: OWLOntology = manager.createOntology()
        val axiomsIS = IOUtils.toInputStream(
            modelToString(ontology), "UTF-8"
        )
        o.addAxioms(manager.loadOntologyFromOntologyDocument(axiomsIS).axioms())
        axiomsIS.close()
        o.saveOntology(System.out)
        return o
    }

    private fun addConfigToOntology(){
        ontology.setNsPrefix("soya", SOYA_URL)
        ontology.setNsPrefix("ontology", ONTOLOGY_URL)
        configuration.entries.forEach { e -> ontology.add(
            ontology.createResource(SOYA_URL+e.key),
            ontology.createProperty(SOYA_URL+"anonymizationType"),
            ontology.createResource(SOYA_URL+e.value)
        ) }
    }
}