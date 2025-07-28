package eu.ownyourdata.anonymisationservice.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import eu.ownyourdata.anonymisationservice.dto.AnonymizationType
import eu.ownyourdata.anonymisationservice.dto.Configuration
import org.apache.jena.ontapi.OntJenaException.IllegalArgument
import org.apache.jena.ontology.OntModel
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.query.QueryFactory
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.Lang
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.RiotException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ConfigObject(modelURL: String) {

    private var configurationModel: OntModel
    private var jsonContent: JsonNode
    var configuration: List<Configuration>

    init {
        try {
            val modelStream = fetchInputStream(modelURL)
            val jsonString = modelStream.bufferedReader().use { it.readText() }
            configurationModel = ModelFactory.createOntologyModel()
            RDFDataMgr.read(configurationModel, jsonString.byteInputStream(), Lang.JSONLD)
            configuration = extractConfig()
            val mapper = ObjectMapper()
            jsonContent = mapper.readTree(jsonString.byteInputStream())
        } catch(e: RiotException) {
            throw IllegalArgument("The configuration is not provided in a valid Json-LD format")
        } catch(e: Exception) {
            throw IllegalArgument(e.localizedMessage)
        }
    }

    fun getAttributeObject(attribute: String): List<String> {
        val test: List<String> = jsonContent["@graph"]
            .filter { node ->  node["domain"] != null && node["domain"].asText() == attribute}
            .map { node -> node["@id"].asText() }
            .toList()
        return test
    }

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

    private fun extractConfig(): List<Configuration> {
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
        QueryExecutionFactory.create(query, configurationModel).use { qe ->
            val result = qe.execSelect()
            return result.asSequence().map { entry -> Configuration(
                extractValueFromURL(entry["attribute"].toString()),
                extractValueFromURL(entry["datatype"].toString()),
                AnonymizationType.convert(extractValueFromURL(entry["anonymization"].toString()))
            ) }.toList()
        }
    }

    private fun extractValueFromURL(url: String): String {
        val lastIndex = maxOf(url.lastIndexOf('/'), url.lastIndexOf('#'))
        return if (lastIndex != -1) url.substring(lastIndex + 1) else url
    }
}

