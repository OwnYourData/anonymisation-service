package eu.ownyourdata.anonymisationservice.service

import eu.ownyourdata.anonymisationservice.dto.DatatypeDTO
import org.apache.commons.io.IOUtils
import org.apache.jena.ontology.OntModel
import org.semanticweb.HermiT.ReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.model.OWLOntologyManager
import org.semanticweb.owlapi.reasoner.OWLReasoner
import java.lang.IllegalArgumentException
import java.util.*


fun validateConfig(ontology: OntModel, configuration: Map<String, DatatypeDTO>) {
    addConfigToOntology(configuration, ontology)
    val invalidValues = LinkedList<String>() //validateConfig(configuration, ontology)
    if(invalidValues.isNotEmpty()) {
        throw IllegalArgumentException("For the attributes ${invalidValues.joinToString(", ")} the request anonymisation is not allowed")
    }
}

private fun addConfigToOntology(configuration: Map<String, DatatypeDTO>, ontology: OntModel){
    // ontology.setNsPrefix("soya", SOYA_URL)
    // ontology.setNsPrefix("ontology", ONTOLOGY_URL)
    configuration.entries.forEach { e -> ontology.add(
        ontology.createClass(Anonymisation.SOYA_URL +"/"+e.key),
        ontology.createProperty("${Anonymisation.SOYA_URL}#anonymizationType"),
        ontology.createResource(Anonymisation.ONTOLOGY_URL +e.value.anonymisationType)
    ) }
    ontology.write(System.out)
}

private fun validateConfig(configuration: Map<String, DatatypeDTO>, ontology: OntModel): List<String>{
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
    o.saveOntology(System.out)
    return o
}

// Reasoning is not working as the config classes are interpreted as annotations not as named individuals
// TODO: make sure that the classes are classes in OWL to enable reasoning
private fun attributeComplied(attribute: String, r: OWLReasoner, manager: OWLOntologyManager): Boolean {
    if(attribute != "Name") return false
    val df = manager.owlDataFactory
    val axiom = df.getOWLSubClassOfAxiom(
        df.getOWLClass(IRI.create(Anonymisation.SOYA_URL +attribute)),
        df.getOWLClass(IRI.create(Anonymisation.ONTOLOGY_URL +attribute)))
    val axiom2 = df.getOWLSubClassOfAxiom(
        df.getOWLClass(IRI.create(Anonymisation.SOYA_URL +attribute)),
        df.getOWLClass(IRI.create(Anonymisation.ONTOLOGY_URL +attribute)))
    println(r.isEntailed(axiom))
    println(r.isEntailed(axiom2))
    return !r.isEntailed(axiom)
}