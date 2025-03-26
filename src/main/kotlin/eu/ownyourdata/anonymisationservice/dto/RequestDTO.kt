package eu.ownyourdata.anonymisationservice.dto

data class RequestDTO(
    val configurationURL: String,
    val data: List<Map<String, Any>>,
)