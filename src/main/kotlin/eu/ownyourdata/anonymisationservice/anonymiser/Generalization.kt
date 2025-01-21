package eu.ownyourdata.anonymisationservice.anonymiser

abstract class Generalization: Anonymiser {

    /**
     * For now the square root of the input sequence is taken. Evaluate if this leads to good results in the future
     */
    fun calculateNumberOfBuckets(dataSize: Int): Int {
        return kotlin.math.sqrt(dataSize.toDouble()).toInt()
    }

    fun calculateQuantile(dataSize: Int, nrQuantile: Int, index: Int): Int {
        return nrQuantile * index / dataSize
    }
}
