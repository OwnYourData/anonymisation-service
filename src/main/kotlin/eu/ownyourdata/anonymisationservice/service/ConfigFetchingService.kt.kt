package eu.ownyourdata.anonymisationservice.service

import eu.ownyourdata.anonymisationservice.dto.Configuration
import org.apache.jena.ontapi.OntJenaException.IllegalArgument
import org.apache.jena.ontology.OntModel
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.query.QueryFactory
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.Lang
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.RiotException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

private fun fetchInputStream(urlString: String): InputStream {
    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    return if (connection.responseCode == HttpURLConnection.HTTP_OK) {
        connection.inputStream
    } else {
        throw IllegalArgument("For the provided URL no Ontology was found")
    }
}

private fun extractConfig(model: Model): List<Configuration> {
    val sparqlQuery = """
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX soya: <https://w3id.org/soya/ns#>
        SELECT ?attribute ?datatype ?anonymization WHERE { 
          ?attribute rdfs:domain <https://soya.ownyourdata.eu/AnonymisationDemo/AnonymisationDemo> .
          ?attribute rdfs:range ?datatype .
          ?attribute <https://w3id.org/soya/ns#classification> ?anonymization . 
        }
    """.trimIndent()
    val query = QueryFactory.create(sparqlQuery)
    QueryExecutionFactory.create(query, model).use { qe ->
        val result = qe.execSelect()
        return result.asSequence().map { entry -> Configuration(
            extractValueFromURL(entry["attribute"].toString()),
            extractValueFromURL(entry["datatype"].toString()),
            extractValueFromURL(entry["anonymization"].toString())
        ) }.toList()
    }
}

private fun extractValueFromURL(url: String): String {
    val lastIndex = maxOf(url.lastIndexOf('/'), url.lastIndexOf('#'))
    return if (lastIndex != -1) url.substring(lastIndex + 1) else url
}


fun fetchConfig(url: String): List<Configuration> {
    try {
        val modelStream = fetchInputStream(url)
        val model: OntModel = ModelFactory.createOntologyModel()
        RDFDataMgr.read(model, modelStream, Lang.JSONLD)
        return extractConfig(model)
    } catch(e: RiotException) {
        throw IllegalArgument("The configuration is not provided in a valid Json-LD format")
    } catch(e: Exception) {
        throw IllegalArgument(e.localizedMessage)
    }
}
