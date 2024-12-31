package eu.ownyourdata.anonymisationservice.anonymiser

import java.util.Date

class GeneralizationDate: Generalization<Date>() {

    override fun anonymise(values: List<Date>): List<Date> {
        // TODO implement generalization for Date
        return values
    }
}