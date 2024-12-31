package eu.ownyourdata.anonymisationservice.anonymiser

fun anonymizerFactory(anonymisationType: String, dataType: String): Anonymiser<*> {
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

interface Anonymiser<T> {

    fun anonymise(values: List<T>): List<T>
}