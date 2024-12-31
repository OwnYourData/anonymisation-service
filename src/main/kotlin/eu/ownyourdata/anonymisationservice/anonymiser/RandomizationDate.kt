package eu.ownyourdata.anonymisationservice.anonymiser

import java.util.*

class RandomizationDate: Randomization<Date>() {

    override fun anonymise(values: List<Date>): List<Date> {
        // TODO: implement randomization for date values
        return values;
    }
}