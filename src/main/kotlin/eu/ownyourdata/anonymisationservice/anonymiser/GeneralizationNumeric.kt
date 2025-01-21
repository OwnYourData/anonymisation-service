package eu.ownyourdata.anonymisationservice.anonymiser
import java.lang.IllegalArgumentException

class GeneralizationNumeric: Generalization() {

    override fun anonymise(values: MutableList<Any>): List<Any> {
        val bucketNumber = calculateNumberOfBuckets(values.size)
        val numericValues = values.mapIndexed { index, it ->
                when (it) {
                    is Double -> doubleArrayOf(it, index.toDouble())
                    is Int -> doubleArrayOf(it.toDouble(), index.toDouble())
                    else -> throw IllegalArgumentException("Numeric Generalization was requested but the input $it is not numeric")
                }
            }
            .sortedBy { v -> v[0] }
            .mapIndexed { index, it -> it + calculateQuantile(values.size, bucketNumber, index).toDouble()}
            .toTypedArray()
        // assign each value to its quantile -> create a Map that assigns a quantile to String interval representation
        // Sort by the ids and map to the values using the created map
        return values
    }
}