package eu.ownyourdata.anonymisationservice.anonymiser

import jakarta.json.Json
import jakarta.json.JsonValue

class Masking: Anonymiser {

    override fun anonymise(values: MutableList<Any>, anonymisationCount: Int): List<JsonValue> {
        return values.stream().map { Json.createValue("*****") }.toList()
    }

    override fun getAttributeName(attribute: String): String {
        return attribute
    }
}