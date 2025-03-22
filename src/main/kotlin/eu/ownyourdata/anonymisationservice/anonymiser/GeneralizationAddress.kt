package eu.ownyourdata.anonymisationservice.anonymiser

import java.lang.IllegalArgumentException

class GeneralizationAddress : Anonymiser {

    /** Address-Schema: "Musterstraße 1, 1010 Wien, Wien, Österreich", Possible future development:
     * Include mapping from Zipcode to country in ontology
     **/

    override fun anonymise(values: MutableList<Any>): List<Any> {

        // TODO include test for city
        val addresses = values.mapIndexed { index, address -> Pair(index, parseAddress(address.toString())) }

        val cityGroup = addresses.groupBy(
            keySelector = { it.second.city },
            valueTransform = { it.first }
        )
        val cities = checkGeneralizationLevel(cityGroup)
        if (!cities.isNullOrEmpty()) return cities

        val stateGroup = addresses.groupBy(
            keySelector = { it.second.state },
            valueTransform = { it.first }
        )
        val states = checkGeneralizationLevel(stateGroup)
        if (!states.isNullOrEmpty()) return states

        val countryGroups = addresses.groupBy(
            keySelector = { it.second.country },
            valueTransform = { it.first }
        )
        val countries = checkGeneralizationLevel(countryGroups)
        if (!countries.isNullOrEmpty()) return countries

        // TODO return "Location" * values.size
        return values
    }

    private fun checkGeneralizationLevel(generalization: Map<String, List<Int>>): List<String>? {
        return if (generalization.values.minOfOrNull { e -> e.size }!! > 2) {
            generalization.flatMap { (str, indices) -> indices.map { index -> str to index } }
                .sortedBy { pair -> pair.second }
                .map { pair -> pair.first }
                .toList()
        } else {
            null
        }
    }

    private fun parseAddress(addressString: String): Address {
        val parts = addressString.split(",").map { it.trim() }
        return if (parts.size == 4) {
            Address(
                city = parts[1],
                state = parts[2],
                country = parts[3]
            )
        } else {
            throw IllegalArgumentException(
                "The value $addressString could not be parsed to an address as the type is " +
                        "not 'Street Nr, Zip-Code City, State, Country"
            )
        }
    }
}

data class Address(
    val city: String,
    val state: String,
    val country: String
)