package eu.ownyourdata.anonymisationservice.anonymiser

import java.util.*

class GeneralizationAddress: Generalization<String>() {
    override fun convertValues(index: Int, value: Any): Pair<Int, String> {
        return Pair(index, "Test Value")
    }

    override fun getQuantileValues(numericValues: SortedMap<Int, Pair<IntArray, List<String>>>): Map<String, IntArray> {
        return numericValues.map {(key, value) ->
            key.toString() to value.first
        }.toMap()
    }
}