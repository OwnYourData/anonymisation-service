package eu.ownyourdata.anonymisationservice.anonymiser

import eu.ownyourdata.anonymisationservice.dto.AnonymizationType
import eu.ownyourdata.anonymisationservice.dto.Configuration
import eu.ownyourdata.anonymisationservice.service.ConfigObject
import jakarta.json.JsonValue
import java.lang.IllegalArgumentException
import kotlin.math.floor
import kotlin.math.pow

fun anonymizerFactory(config: Configuration, configObject: ConfigObject): Anonymiser {
    return when(config.anonaymizationType) {
        AnonymizationType.MASKING -> Masking()
        AnonymizationType.GENERALIZATION -> when (config.datatype.lowercase()) {
            "date" -> GeneralizationDate()
            "integer" -> GeneralizationNumeric()
            "string" -> throw IllegalArgumentException("The Datatype String is not allowed for generalization")
            else -> GeneralizationObject(configObject.getAttributeObject(config.datatype))
        }
        AnonymizationType.RANDOMIZATION -> when (config.datatype) {
            "date" -> RandomizationDate()
            "integer" -> RandomizationNumeric()
            else -> throw IllegalArgumentException("The Datatype ${config.datatype} is not allowed for randomization")
        }
    }
}

interface Anonymiser {

    /**
     * The function has the input and return parameter any as the input validation takes place in the specific functions
     */
    fun anonymise(values: MutableList<Any>, anonymisationCount: Int): List<JsonValue>

    fun getAttributeName(attribute: String): String

    fun anonymiseWithNulls(values: MutableList<Any?>, anonymisationCount: Int): List<JsonValue?> {
        val nulls: List<Boolean> = values.stream().map { v -> v == null }.toList()
        val noNullsValues: MutableList<Any> = values.filterNotNull().toMutableList()
        val anonymizedValues = anonymise(noNullsValues, anonymisationCount)
        var positionNextValue = 0
        return nulls.stream().map { v ->
            if (v) {
                null
            } else {
                anonymizedValues[positionNextValue++]
            }
        }.toList()
    }

    fun calculateNumberOfBuckets(dataSize: Int, numberAttributes: Int): Int {
        return floor(1.0 /
                (1.0 - (1.0 - 0.99.pow(1.0 / dataSize)).pow(1.0 / dataSize)).pow(1.0 / numberAttributes)
        ).toInt()
    }
}