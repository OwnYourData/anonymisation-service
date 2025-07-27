package eu.ownyourdata.anonymisationservice.anonymiser

import jakarta.json.Json
import jakarta.json.JsonValue
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

class GeneralizationDate: Generalization<LocalDate>() {

    override fun convertValues(index: Int, value: Any): Pair<Int, LocalDate> {
        return when (value) {
            is String -> runCatching {
                Pair(index, LocalDate.parse(value))
            }.getOrElse {
                throw IllegalArgumentException("Invalid date format for value: '$value'. Expected format: yyyy-MM-dd")
            }
            is LocalDate -> Pair(index, value)
            else ->
                throw IllegalArgumentException("Date Generalization was requested but the input $value is not numeric")
        }
    }

    override fun getQuantileValues(quantiles: SortedMap<Int, Pair<IntArray, List<LocalDate>>>):
            Map<JsonValue, IntArray> {
        val quantileValues = HashMap<JsonValue, IntArray>()
        for (i in 0 until quantiles.size) {
            val values = quantiles[i]?.second
                ?: throw IllegalArgumentException("The required quantile is not defined in the input")
            val indices = quantiles[i]?.first
                ?: throw IllegalArgumentException("The required quantile is not defined in the input")
            val nextVal = quantiles[i + 1]?.second?.min()
            val prevVal = quantiles[i - 1]?.second?.max()
            val upperBound = if (nextVal != null) {
                values.max().plusDays(ChronoUnit.DAYS.between(values.max(), nextVal)/2)
            } else values.max()
            val lowerBound = if (prevVal != null) {
                values.min().plusDays(ChronoUnit.DAYS.between(values.min(), prevVal)/2)
            } else values.min()
            val resultJson = Json.createObjectBuilder()
            resultJson.add("max", upperBound.toString())
            resultJson.add("min", lowerBound.toString())
            quantileValues[resultJson.build()] = indices
        }
        return quantileValues
    }

    override fun getAttributeName(attribute: String): String {
        return attribute + "_date_range"
    }
}