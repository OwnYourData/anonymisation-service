package eu.ownyourdata.anonymisationservice.anonymiser

import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class RandomizationDate: Randomization() {

    override fun anonymise(values: MutableList<Any>, anonymisationCount: Int): List<Any> {
        val dateValues = values.stream().map { value ->
            when (value) {
                is String -> runCatching {
                    LocalDate.parse(value)
                }.getOrElse {
                    throw IllegalArgumentException("Invalid date format for value: '$value'. Expected format: yyyy-MM-dd")
                }
                is LocalDate -> value
                else ->
                    throw IllegalArgumentException("Invalid date format for value: '$value'. Expected format: yyyy-MM-dd")
            }
        }.toList()
        val sd = calculateSDOfDates(dateValues)
        return dateValues.map { d -> addRandomNoise(sd, values.size, d) }
    }

    private fun addRandomNoise(sd: Double, size: Int, value: LocalDate): LocalDate {
        return value.plusDays(
            (Random().nextGaussian() * PRIVACY_FACTOR * (sd / sqrt(size.toDouble()))).toLong())
    }

    private fun calculateSDOfDates(dates: List<LocalDate>): Double {
        val minDate = dates.minOrNull() ?: return 0.0
        val daysList = dates.map { d -> ChronoUnit.DAYS.between(minDate, d).toDouble() }
        val mean = daysList.average()
        return sqrt(daysList.map { (it - mean).pow(2) }.average())
    }
}