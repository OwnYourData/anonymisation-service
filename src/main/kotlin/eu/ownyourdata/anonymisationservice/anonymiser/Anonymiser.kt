package eu.ownyourdata.anonymisationservice.anonymiser

import java.lang.IllegalArgumentException

fun anonymizerFactory(anonymisationType: String, dataType: String): Anonymiser {
    if(anonymisationType == "Masking") {
        return Masking()
    } else if(anonymisationType == "Generalization") {
        return when (dataType) {
            "Date" -> GeneralizationDate()
            "Numeric" -> GeneralizationNumeric()
            "Address" -> GeneralizationAddress()
            else -> throw IllegalArgumentException("The Datatype $dataType is not allowed for generalization")
        }
    } else {
        return when (dataType) {
            "Date" -> RandomizationDate()
            "Numeric" -> RandomizationNumeric()
            else -> throw IllegalArgumentException("The Datatype $dataType is not allowed for randomization")
        }
    }
}

interface Anonymiser {


    /**
     * The function has the input and return parameter any as the input validation takes place in the specific functions
     */
    fun anonymise(values: MutableList<Any>): List<Any>
}