package eu.ownyourdata.anonymisationservice.dto

data class Configuration (
    val attribute: String,
    val datatype: String,
    val anonaymizationType: AnonymizationType,
)