package eu.ownyourdata.anonymisationservice.anonymiser
import java.lang.IllegalArgumentException
import java.util.*

class GeneralizationNumeric: Generalization<Double>() {

    override fun getQuantileValues(numericValues: SortedMap<Int, Pair<IntArray, List<Double>>>): Map<String, IntArray> {
        val quantileValues = HashMap<String, IntArray>()
        for (i in 0 until numericValues.size) {
            val values = numericValues[i]?.second
                ?: throw IllegalArgumentException("The required quantile is not defined in the input")
            val indices = numericValues[i]?.first
                ?: throw IllegalArgumentException("The required quantile is not defined in the input")
            val nextVal = numericValues[i + 1]?.second?.min()
            val prevVal = numericValues[i - 1]?.second?.max()
            val upperBound = if (nextVal != null) {
                (values.max() + nextVal) / 2
            } else values.max()
            val lowerBound = if (prevVal != null) {
                (values.min() + prevVal) / 2
            } else values.min()
            if (i == 0) {
                quantileValues["<= $upperBound"] = indices
            } else if (i == numericValues.size - 1) {
                quantileValues[">= $lowerBound"] = indices
            } else {
                quantileValues["[$lowerBound, $upperBound]"] = indices
            }
        }
        return quantileValues
    }

    override fun convertValues(index: Int, value: Any): Pair<Int, Double> {
        when (value) {
            is Double -> return Pair(index, value)
            is Int -> return Pair(index, value.toDouble())
            else -> throw IllegalArgumentException("Numeric Generalization was requested but the input $value is not numeric")
        }
    }

}