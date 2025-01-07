package eu.ownyourdata.anonymisationservice.service

import org.apache.jena.ontology.OntModel
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.Lang
import org.apache.jena.riot.RDFDataMgr
import java.io.File
import java.io.InputStream
import java.io.StringWriter

fun getOntology(url: String): OntModel {
    // TODO for now get content from file but change to request
    val fileContent: InputStream = File("Beispiele/anonymization_ontology.jsonld").inputStream()
    val ontModel: OntModel = ModelFactory.createOntologyModel()
    RDFDataMgr.read(ontModel, fileContent, null, Lang.JSONLD)
    return ontModel
}

// TODO potentially delete
fun getModel(modelString: Map<String, String>): Model {
    val model = ModelFactory.createDefaultModel()
    model.setNsPrefix("soya", "https://soya.ownyourdata.eu/OntologyEEG/soya")
    modelString.entries.forEach { config -> model.add(
        model.createResource("soya:"+config.key),
        model.createProperty("soya:anonymizationType"),
        model.createResource(config.value)
    ) }
    return model
}

fun modelToString(model: Model): String {
    val writer = StringWriter()
    model.write(writer, Lang.TURTLE.name)
    return writer.toString()
}


