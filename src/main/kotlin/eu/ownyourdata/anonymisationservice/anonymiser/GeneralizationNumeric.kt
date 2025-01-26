package eu.ownyourdata.anonymisationservice.anonymiser
import java.lang.IllegalArgumentException
import java.util.*

class GeneralizationNumeric: Generalization() {

    override fun anonymise(values: MutableList<Any>): List<Any> {
        val bucketNumber = calculateNumberOfBuckets(values.size)
        val quantiles: SortedMap<Double, Pair<DoubleArray, DoubleArray>> =  values.mapIndexed { index, it ->
                when (it) {
                    is Double -> doubleArrayOf(it, index.toDouble())
                    is Int -> doubleArrayOf(it.toDouble(), index.toDouble())
                    else -> throw IllegalArgumentException("Numeric Generalization was requested but the input $it is not numeric")
                }
            }.sortedBy { v -> v[0] }
            .mapIndexed { index, it -> it + calculateQuantile(values.size, bucketNumber, index).toDouble()}
            .groupBy(
                keySelector = { it[2] },
                valueTransform = { it.sliceArray(0..1) }
            ).mapValues { (_, values) ->
                val inputValues = values.map { it[0] }.toDoubleArray()
                val positions = values.map { it[1] }.toDoubleArray()
                Pair(inputValues, positions)
            }.toSortedMap()
        return getQuantileValues(quantiles)
            .flatMap { (str, indices) -> indices.map { index -> str to index }}
            .sortedBy { pair -> pair.second }
            .map { pair -> pair.first }
            .toList()
    }

    private fun getQuantileValues(numericValues: SortedMap<Double, Pair<DoubleArray, DoubleArray>>): Map<String, DoubleArray> {
        val quantileValues = HashMap<String, DoubleArray>()
        for (i in 0 until numericValues.size) {
            val values = numericValues[i.toDouble()]?.first
                ?: throw IllegalArgumentException("The required quantile is not defined in the input")
            val indices = numericValues[i.toDouble()]?.second
                ?: throw IllegalArgumentException("The required quantile is not defined in the input")
            val nextVal = numericValues[i + 1.0]?.first?.min()
            val prevVal = numericValues[i - 1.0]?.first?.max()
            val upperBound = if (nextVal != null) {
                (values.max() + nextVal) / 2
            } else values.max()
            val lowerBound = if(prevVal != null) {
                (values.min() + prevVal) / 2
            } else values.min()
            if (i == 0) {
                quantileValues["<= $upperBound"] = indices
            } else if(i == numericValues.size-1) {
                quantileValues[">= $lowerBound"] = indices
            } else {
                quantileValues["[$lowerBound, $upperBound]"] = indices
            }
        }
        return quantileValues
    }
}