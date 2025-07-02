package eu.ownyourdata.anonymisationservice.anonymiser

import jakarta.json.Json
import jakarta.json.JsonValue
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

class RandomizationDate: Randomization<LocalDate>() {

    override fun anonymise(values: MutableList<Any>, anonymisationCount: Int): List<JsonValue> {
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
        val minimumDate = dateValues.min()
        val maximumDate = dateValues.max()

        val distances = createDistancePerInstance(
            dateValues,
            values.size/calculateNumberOfBuckets(values.size, anonymisationCount)
        )
        return distances.map { v -> Json.createValue(calculateNoise(minimumDate, maximumDate, v.first, v.second).toString())}
    }

    private fun calculateNoise(min: LocalDate, max: LocalDate, value: LocalDate, distance: Double): LocalDate {
        val noiseDays = (Random().nextGaussian() * distance).toLong()
        val noisyDate = value.plusDays(noiseDays)

        return when {
            noisyDate.isBefore(min) -> {
                val delta = java.time.temporal.ChronoUnit.DAYS.between(noisyDate, min)
                min.plusDays(delta)
            }
            noisyDate.isAfter(max) -> {
                val delta = java.time.temporal.ChronoUnit.DAYS.between(max, noisyDate)
                max.minusDays(delta)
            }
            else -> noisyDate
        }
    }

    override fun distance(val1: LocalDate, val2: LocalDate): Double {
        return ChronoUnit.DAYS.between(val1, val2).toDouble()
    }
}