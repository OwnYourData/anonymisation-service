package eu.ownyourdata.anonymisationservice.anonymiser
import jakarta.json.JsonValue
import java.util.*

abstract class Generalization<T : Comparable<T>>: Anonymiser {

    override fun anonymise(values: MutableList<Any>, anonymisationCount: Int): List<JsonValue> {
        val bucketNumber = calculateNumberOfBuckets(values.size, anonymisationCount)
        val quantiles: SortedMap<Int, Pair<IntArray, List<T>>> =  values.mapIndexed {
                index, it -> convertValues(index, it)
        }.sortedBy { v -> v.second }
            .mapIndexed { index, it -> Triple(it.first, it.second, calculateQuantile(values.size, bucketNumber, index))}
            .groupBy(
                keySelector = { it.third },
                valueTransform = { it.first to it.second }
            ).mapValues { (_, values) ->
                val positions = values.map { it.first }.toIntArray()
                val inputValues = values.map { it.second }
                Pair(positions, inputValues)
            }.toSortedMap()
        return getQuantileValues(quantiles)
            .flatMap { (str, indices) -> indices.map { index -> str to index }}
            .sortedBy { pair -> pair.second }
            .map { pair -> pair.first }
            .toList()
    }

    private fun calculateQuantile(dataSize: Int, nrQuantile: Int, index: Int): Int {
        return nrQuantile * index / dataSize
    }

    abstract fun convertValues(index: Int, value: Any): Pair<Int, T>

    abstract fun getQuantileValues(quantiles: SortedMap<Int, Pair<IntArray, List<T>>>): Map<JsonValue, IntArray>
}
