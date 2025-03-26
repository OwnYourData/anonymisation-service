package eu.ownyourdata.anonymisationservice.anonymiser

import java.lang.IllegalArgumentException

fun anonymizerFactory(anonymisationType: String, dataType: String): Anonymiser {
    if(anonymisationType.lowercase() == "masking") {
        return Masking()
    } else if(anonymisationType.lowercase() == "generalization") {
        return when (dataType.lowercase()) {
            "date" -> GeneralizationDate()
            "integer" -> GeneralizationNumeric()
            "address" -> GeneralizationAddress()
            else -> throw IllegalArgumentException("The Datatype $dataType is not allowed for generalization")
        }
    } else {
        return when (dataType.lowercase()) {
            "date" -> RandomizationDate()
            "integer" -> RandomizationNumeric()
            else -> throw IllegalArgumentException("The Datatype $dataType is not allowed for randomization")
        }
    }
}

interface Anonymiser {

    /**
     * The function has the input and return parameter any as the input validation takes place in the specific functions
     */
    fun anonymise(values: MutableList<Any>): List<Any>

    fun anonymiseWithNulls(values: MutableList<Any?>): List<Any?> {
        val nulls: List<Boolean> = values.stream().map { v -> v == null }.toList()
        val noNullsValues: MutableList<Any> = values.filterNotNull().toMutableList()
        val anonymizedValues = anonymise(noNullsValues)
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