package eu.ownyourdata.anonymisationservice.anonymiser
import java.util.*
import kotlin.math.floor
import kotlin.math.pow

abstract class Generalization<T : Comparable<T>>: Anonymiser {

    override fun anonymise(values: MutableList<Any>, anonymisationCount: Int): List<Any> {
        val bucketNumber = calculateNumberOfBuckets(values.size, anonymisationCount)
        println("Another test instance")
        println("k: ${values.size}")
        println("n: $anonymisationCount")
        println("x: $bucketNumber")

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

    /**
     * For now the square root of the input sequence is taken. Evaluate if this leads to good results in the future
     */
    private fun calculateNumberOfBuckets(dataSize: Int, numberAttributes: Int): Int {
        return floor(1.0 /
                (1.0 - (1.0 - 0.95.pow(1.0 / dataSize)).pow(1.0 / dataSize)).pow(1.0 / numberAttributes)
        ).toInt()
    }

    private fun calculateQuantile(dataSize: Int, nrQuantile: Int, index: Int): Int {
        return nrQuantile * index / dataSize
    }

    abstract fun convertValues(index: Int, value: Any): Pair<Int, T>

    abstract fun getQuantileValues(quantiles: SortedMap<Int, Pair<IntArray, List<T>>>): Map<String, IntArray>
}
