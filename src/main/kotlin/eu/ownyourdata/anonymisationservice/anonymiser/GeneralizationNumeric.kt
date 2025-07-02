package eu.ownyourdata.anonymisationservice.anonymiser
import com.fasterxml.jackson.databind.JsonNode
import jakarta.json.Json
import jakarta.json.JsonObject
import jakarta.json.JsonValue
import java.lang.IllegalArgumentException
import java.util.*

class GeneralizationNumeric: Generalization<Double>() {

    override fun convertValues(index: Int, value: Any): Pair<Int, Double> {
        return when (value) {
            is Double -> Pair(index, value)
            is Int -> Pair(index, value.toDouble())
            is String -> runCatching {
                Pair(index, value.toDouble())
            }.getOrElse {
                throw IllegalArgumentException("Numeric Generalization was requested but the input $value is not numeric")
            }
            else -> throw IllegalArgumentException("Numeric Generalization was requested but the input $value is not numeric")
        }
    }

    override fun getQuantileValues(quantiles: SortedMap<Int, Pair<IntArray, List<Double>>>): Map<JsonValue, IntArray> {
        val quantileValues = HashMap<JsonValue, IntArray>()
        for (i in 0 until quantiles.size) {
            val values = quantiles[i]?.second
                ?: throw IllegalArgumentException("The required quantile is not defined in the input")
            val indices = quantiles[i]?.first
                ?: throw IllegalArgumentException("The required quantile is not defined in the input")
            val nextVal = quantiles[i + 1]?.second?.min()
            val prevVal = quantiles[i - 1]?.second?.max()
            val upperBound = if (nextVal != null) {
                (values.max() + nextVal) / 2
            } else values.max()
            val lowerBound = if (prevVal != null) {
                (values.min() + prevVal) / 2
            } else values.min()
            val resultJson = Json.createObjectBuilder()
            if (i != 0) {
                resultJson.add("min", lowerBound)
            }
            if (i != quantiles.size - 1) {
                resultJson.add("max", upperBound)
            }
            quantileValues[resultJson.build()] = indices
        }
        return quantileValues
    }

}