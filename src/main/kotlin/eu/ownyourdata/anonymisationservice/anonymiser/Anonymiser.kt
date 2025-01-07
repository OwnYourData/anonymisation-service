package eu.ownyourdata.anonymisationservice.anonymiser

fun anonymizerFactory(anonymisationType: String, dataType: String): Anonymiser {
    if(anonymisationType == "Masking") {
        return Masking()
    } else if(anonymisationType == "Generalization") {
        return if(dataType == "Date") {
            GeneralizationDate()
        } else {
            GeneralizationInteger()
        }
    } else {
        return if(dataType == "Date") {
            RandomizationDate()
        } else {
            RandomizationInteger()
        }
    }
}

interface Anonymiser {


    /**
     * The function has the input and return parameter any as the input validation takes place in the specific functions
     */
    fun anonymise(values: MutableList<Any>): List<Any>
}