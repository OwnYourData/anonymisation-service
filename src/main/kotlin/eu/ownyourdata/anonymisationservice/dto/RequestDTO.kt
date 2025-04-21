package eu.ownyourdata.anonymisationservice.dto

import io.swagger.v3.oas.annotations.media.Schema

data class RequestDTO(

    @field:Schema(description = "The configuration URL", example = "https://soya.ownyourdata.eu/AnonymisationDemo")
    val configurationURL: String,

    @field:Schema(
        description = "Data to be anonymized",
        example = """
            [
                {
                    "Name": "Name 1",
                    "Geburtsdatum": "1975-11-01",
                    "Adresse": "Musterstraße 1, 1010 Niederösterreich, Niederösterreich, Österreich",
                    "Gehalt": 10000
                },
                {
                    "Name": "Name 2",
                    "Geburtsdatum": "1985-12-12",
                    "Gehalt": 100000
                },
                {
                    "Name": "Name 3",
                    "Adresse": "Musterstraße 1, 1010 Niederösterreich, Niederösterreich, Österreich",
                    "Gehalt": 40000
                },
                {
                    "Name": "Name 4",
                    "Geburtsdatum": "1950-07-07",
                    "Adresse": "Musterstraße 1, 1010 Wien, Wien, Österreich"
                },
                {
                    "Geburtsdatum": "1990-01-01",
                    "Adresse": "Musterstraße 1, 1010 Wien, Wien, Österreich",
                    "Gehalt": 45000
                },
                {
                    "Name": "Name 6",
                    "Geburtsdatum": "2019-05-14",
                    "Gehalt": 12000
                },
                {
                    "Name": "Name 7",
                    "Geburtsdatum": "1974-01-01",
                    "Adresse": "Musterstraße 1, 1010 Wien, Wien, Österreich",
                    "Gehalt": 10000
                },
                {
                    "Name": "Name 8",
                    "Geburtsdatum": "1966-06-06",
                    "Adresse": "Musterstraße 1, 1010 Wien, Wien, Österreich",
                    "Gehalt": 30000
                },
                {
                    "Name": "Name 9",
                    "Geburtsdatum": "1979-01-25",
                    "Adresse": "Musterstraße 1, 1010 Wien, Wien, Österreich"
                },
                {
                    "Name": "Name 10",
                    "Geburtsdatum": "1949-11-01",
                    "Gehalt": 20000
                }
            ]
        """
    )
    val data: List<Map<String, Any>>,
)