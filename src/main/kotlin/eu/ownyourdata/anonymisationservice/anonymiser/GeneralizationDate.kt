package eu.ownyourdata.anonymisationservice.anonymiser

import java.util.*

class GeneralizationDate: Generalization<Date>() {

    override fun convertValues(index: Int, value: Any): Pair<Int, Date> {
        return Pair(index, Date())
    }

    override fun getQuantileValues(numericValues: SortedMap<Int, Pair<IntArray, List<Date>>>): Map<String, IntArray> {
        return numericValues.map {(key, value) ->
            key.toString() to value.first
        }.toMap()
    }
}