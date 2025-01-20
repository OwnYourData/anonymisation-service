package eu.ownyourdata.anonymisationservice.anonymiser

class Masking: Anonymiser {

    override fun anonymise(values: MutableList<Any>): List<Any> {
        return values.stream().map { "*****" }.toList()
    }
}