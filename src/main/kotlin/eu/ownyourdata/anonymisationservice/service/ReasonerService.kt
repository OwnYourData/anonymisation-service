package eu.ownyourdata.anonymisationservice.service

import eu.ownyourdata.anonymisationservice.dto.DatatypeDTO
import org.apache.commons.io.IOUtils
import org.apache.jena.ontology.OntModel
import org.apache.jena.vocabulary.OWL
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.model.OWLOntologyManager
import org.semanticweb.owlapi.reasoner.OWLReasoner

fun validateConfig(ontology: OntModel, configuration: Map<String, DatatypeDTO>) {
    addConfigToOntology(configuration, ontology)
    val invalidValues = validateConfigOptions(ontology, configuration)
    if(invalidValues.isNotEmpty()) {
        throw IllegalArgumentException("For the attributes ${invalidValues.joinToString(", ")} " +
                "the requested anonymisation is not allowed")
    }
}

private fun addConfigToOntology(configuration: Map<String, DatatypeDTO>, ontology: OntModel){
    configuration.entries.forEach { e ->
        ontology.createClass("${Anonymisation.SOYA_URL}/${e.key}")
            .addProperty(OWL.equivalentClass, ontology.createResource(OWL.Restriction)
                .addProperty(OWL.onProperty, ontology.createResource("${Anonymisation.SOYA_URL}#anonymizationType"))
                .addProperty(OWL.someValuesFrom, ontology.createClass().addProperty(OWL.oneOf, ontology.createList()
                    .with(ontology.createClass("${Anonymisation.SOYA_URL}#${e.value.anonymisationType}")))))
    }
}

private fun validateConfigOptions(ontology: OntModel, configuration: Map<String, DatatypeDTO>): List<String>{
    val manager = OWLManager.createOWLOntologyManager()
    val o = createOWLOntology(manager, ontology)
    val r = ReasonerFactory().createReasoner(o)
    return configuration.keys.stream()
        .filter { attr: String -> attributeComplied(attr, r, manager) }
        .toList()
}

private fun createOWLOntology(manager: OWLOntologyManager, ontology: OntModel): OWLOntology {
    val o: OWLOntology = manager.createOntology()
    val axiomsIS = IOUtils.toInputStream(
        modelToString(ontology), "UTF-8"
    )
    o.addAxioms(manager.loadOntologyFromOntologyDocument(axiomsIS).axioms())
    axiomsIS.close()
    return o
}

private fun attributeComplied(attribute: String, r: OWLReasoner, manager: OWLOntologyManager): Boolean {
    val df = manager.owlDataFactory
    val axiom = df.getOWLSubClassOfAxiom(
        df.getOWLClass(IRI.create("${Anonymisation.SOYA_URL}/${attribute}")),
        df.getOWLClass(IRI.create("${Anonymisation.ONTOLOGY_URL}/${attribute}")))
    return !r.isEntailed(axiom)
}