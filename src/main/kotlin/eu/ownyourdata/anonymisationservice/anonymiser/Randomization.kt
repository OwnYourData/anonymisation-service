package eu.ownyourdata.anonymisationservice.anonymiser

import java.util.*
import kotlin.math.abs
import kotlin.math.max

abstract class Randomization<T: Comparable<T>>: Anonymiser {

    abstract fun distance(val1: T, val2: T): Double

    protected fun createDistancePerInstance(values: List<T>,
                                            x: Int): List<Pair<T, Double>> {
        val adjustedX = if (x == values.size) x - 1 else x
        val result: List<Pair<Int, T>> = values
            .withIndex()
            .sortedBy { it.value }
            .map { Pair(it.index, it.value) }
        var lowerBound = 0
        val distances = LinkedList<Pair<Int, Double>>()
        for ((index, value) in result.withIndex()) {
            if (x == values.size)
            while (
                lowerBound < values.size - (adjustedX + 1) && (
                        lowerBound < index - adjustedX ||
                                abs(distance(result[lowerBound].second, value.second)) >
                                abs(distance(result[lowerBound+adjustedX+1].second, value.second))
                        )
            ) {
                lowerBound ++
            }
            val distance = max(
                abs(distance(result[lowerBound].second, value.second)),
                abs(distance(result[lowerBound+adjustedX].second, value.second))
            )
            distances.add(Pair(value.first, distance))
        }
        return distances
            .sortedBy{ it.first }
            .map { p -> Pair(values[p.first], p.second) }
    }

    override fun getAttributeName(attribute: String): String {
        return attribute
    }
}