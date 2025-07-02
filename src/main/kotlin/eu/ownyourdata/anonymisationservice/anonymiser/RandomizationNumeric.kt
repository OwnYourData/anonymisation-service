package eu.ownyourdata.anonymisationservice.anonymiser

import jakarta.json.Json
import jakarta.json.JsonValue
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.util.Random

class RandomizationNumeric: Randomization<Double>() {
    override fun anonymise(values: MutableList<Any>, anonymisationCount: Int): List<JsonValue> {
        val numericValues = values.stream().map { value ->
            when (value) {
                is Double -> value
                is Int -> value.toDouble()
                is String -> runCatching {
                    value.toDouble()
                }.getOrElse {
                    throw IllegalArgumentException("Numeric Generalization was requested but the input $value is not numeric")
                }
                else -> throw IllegalArgumentException("Numeric Generalization was requested but the input $value is not numeric")
            }
        }.toList()
        val minimum = numericValues.min()
        val maximum = numericValues.max()
        val distances = createDistancePerInstance(
            numericValues,
            values.size/calculateNumberOfBuckets(values.size, anonymisationCount)
        )
        return distances.map { v -> Json.createValue(calculateNoise(minimum, maximum, v.first, v.second)) }
    }

    private fun calculateNoise(min: Double, max: Double, value: Double, distance: Double): Double {
        val noise = Random().nextGaussian() * distance
        return when {
            value + noise < min || value + noise > max -> value - noise
            else -> value + noise
        }
    }

    override fun distance(val1: Double, val2: Double): Double {
        return val1 - val2
    }
}