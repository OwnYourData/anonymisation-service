package eu.ownyourdata.anonymisationservice.dto

data class RequestDTO(
    val ontology: String,
    val data: List<Map<String, Any>>,
    val configuration: Map<String, DatatypeDTO>
)