package eu.ownyourdata.anonymisationservice.anonymiser

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.json.Json
import jakarta.json.JsonValue


open class GeneralizationObject(objectAttributes: List<String>) : Anonymiser {

    private var objectAttributes: List<String>
    init {
        this.objectAttributes = objectAttributes
    }

    override fun anonymise(values: MutableList<Any>, anonymisationCount: Int): List<JsonValue> {
        val objectValues = values.mapIndexed { index, value ->
            try {
                val map = value as? Map<*, *>
                map!!.mapKeys { (key, _) -> key.toString() }
                Pair(index, map.mapKeys { (key, _) -> key.toString() })
            } catch (e: Exception) {
                throw IllegalArgumentException("Values could not be extracted from attribute.")
            }
        }

        for (attribute in objectAttributes) {
            val groups = objectValues.groupBy(
                keySelector = { it.second[attribute].toString() },
                valueTransform = { it.first }
            )
            val vals = checkGeneralizationLevel(groups)
            if (!vals.isNullOrEmpty()) return vals.stream().map { value -> Json.createValue(value) }.toList()
        }
        return values.stream().map { Json.createValue("*****") }.toList()
    }

    override fun getAttributeName(attribute: String): String {
        return attribute
    }

    private fun checkGeneralizationLevel(generalization: Map<String, List<Int>>): List<String>? {
        return if (generalization.values.minOfOrNull { e -> e.size }!! > 2) {
            generalization.flatMap { (str, indices) -> indices.map { index -> str to index } }
                .sortedBy { pair -> pair.second }
                .map { pair -> pair.first }
                .toList()
        } else {
            null
        }
    }

    private fun parseJSON(objectString: String): JsonNode {
        try {
            val mapper = ObjectMapper()
            return mapper.readTree(objectString)
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "The value $objectString could not be parsed to an JSON object."
            )
        }
    }
}