package eu.ownyourdata.anonymisationservice.anonymiser

import eu.ownyourdata.anonymisationservice.dto.AnonymizationType
import eu.ownyourdata.anonymisationservice.dto.Configuration
import eu.ownyourdata.anonymisationservice.service.ConfigObject
import java.lang.IllegalArgumentException

fun anonymizerFactory(config: Configuration, configObject: ConfigObject): Anonymiser {
    return when(config.anonaymizationType) {
        AnonymizationType.MASKING -> Masking()
        AnonymizationType.GENERALIZATION -> when (config.datatype.lowercase()) {
            "date" -> GeneralizationDate()
            "integer" -> GeneralizationNumeric()
            "string" -> throw IllegalArgumentException("The Datatype String is not allowed for generalization")
            else -> GeneralizationObject(configObject.getAttributeObject(config.datatype.lowercase()))
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
    fun anonymise(values: MutableList<Any>, anonymisationCount: Int): List<Any>

    fun anonymiseWithNulls(values: MutableList<Any?>, anonymisationCount: Int): List<Any?> {
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
}