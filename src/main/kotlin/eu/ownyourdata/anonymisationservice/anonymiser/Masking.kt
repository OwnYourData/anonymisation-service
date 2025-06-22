package eu.ownyourdata.anonymisationservice.anonymiser

class Masking: Anonymiser {

    override fun anonymise(values: MutableList<Any>, anonymisationCount: Int): List<Any> {
        return values.stream().map { "*****" }.toList()
    }
}