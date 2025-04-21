package eu.ownyourdata.anonymisationservice.dto

import java.lang.IllegalArgumentException

enum class AnonymizationType(val label: String) {
    GENERALIZATION("Generalization"),
    RANDOMIZATION("Randomization"),
    MASKING("Masking");

    companion object {
        fun convert(value: String): AnonymizationType {
            return entries.find { it.label.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("The value $value is not valid attribute type")
        }
    }
}