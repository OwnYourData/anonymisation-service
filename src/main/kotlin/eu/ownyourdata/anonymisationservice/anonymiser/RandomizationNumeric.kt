package eu.ownyourdata.anonymisationservice.anonymiser

import java.lang.IllegalArgumentException
import java.util.Random
import kotlin.math.pow
import kotlin.math.sqrt

class RandomizationNumeric: Randomization() {

    /**
     * Eventuell noch eine unterschiedliche Implementierung für ratio und intervalue skaliert
     */

    override fun anonymise(values: MutableList<Any>): List<Any> {
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
        }.toList().toDoubleArray()
        val mean = numericValues.average()
        val sd = sqrt(numericValues.map { (it - mean).pow(2) }.average())
        return numericValues.map { v -> addRandomNoise(sd, values.size, v) }
    }

    private fun addRandomNoise(sd: Double, size: Int, value: Double): Double{
        return value + Random().nextGaussian() * PRIVACY_FACTOR * (sd / sqrt(size.toDouble()))
    }
}