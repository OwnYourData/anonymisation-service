package eu.ownyourdata.anonymisationservice.anonymiser

import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*


object App {
    @JvmStatic
    fun main(args: Array<String>) {
        val randomization = RandomizationDate()
        val dates: MutableList<Any> = mutableListOf()
        dates.add(LocalDate.of(2000,1,1))
        dates.add(LocalDate.of(2000,1,31))
        dates.add(LocalDate.of(2000,6,30))
        dates.add(LocalDate.of(2002,1,1))
        dates.add(LocalDate.of(2002,9,30))
        dates.add(LocalDate.of(2003,1,15))
        dates.add(LocalDate.of(2003,4,30))
        dates.add(LocalDate.of(2004,2,15))
        randomization.anonymise(dates, 4)
    }
}

class RandomizationDate: Randomization<LocalDate>() {

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
        val distances = createDistancePerInstance(dateValues, values.size/anonymisationCount)
        return distances.map { v -> v.first.plusDays((Random().nextGaussian() * v.second).toLong()) }
    }

    override fun distance(val1: LocalDate, val2: LocalDate): Double {
        return ChronoUnit.DAYS.between(val1, val2).toDouble()
    }
}