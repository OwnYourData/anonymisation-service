package eu.ownyourdata.anonymisationservice.anonymiser

import java.lang.IllegalArgumentException
import java.util.Random

class RandomizationNumeric: Randomization<Double>() {
    override fun anonymise(values: MutableList<Any>, anonymisationCount: Int): List<Any> {
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
        val distances = createDistancePerInstance(numericValues, values.size/anonymisationCount)
        return distances.map { v -> v.first + Random().nextGaussian() * v.second }
    }

    override fun distance(val1: Double, val2: Double): Double {
        return val1 - val2
    }
}