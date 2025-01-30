package eu.ownyourdata.anonymisationservice.anonymiser

import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class GeneralizationDate: Generalization<LocalDate>() {

    override fun convertValues(index: Int, value: Any): Pair<Int, LocalDate> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return when (value) {
            is String -> Pair(index, LocalDate.parse(value, formatter))
            is LocalDate -> Pair(index, value)
            else ->
                throw IllegalArgumentException("Date Generalization was requested but the input $value is not numeric")
        }
    }

    override fun getQuantileValues(quantiles: SortedMap<Int, Pair<IntArray, List<LocalDate>>>):
            Map<String, IntArray> {
        val quantileValues = HashMap<String, IntArray>()
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
            when (i) {
                0 -> quantileValues["before $upperBound"] = indices
                quantiles.size - 1 -> quantileValues["after $lowerBound"] = indices
                else -> quantileValues["between $lowerBound and $upperBound"] = indices
            }
        }
        return quantileValues
    }
}