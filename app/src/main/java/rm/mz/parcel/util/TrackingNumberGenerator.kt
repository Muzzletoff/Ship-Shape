package rm.mz.parcel.util

import kotlin.random.Random

object TrackingNumberGenerator {
    private const val CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    private const val PREFIX = "PCL"
    private const val LENGTH = 8

    fun generate(): String {
        val randomPart = (1..LENGTH)
            .map { CHARS[Random.nextInt(0, CHARS.length)] }
            .joinToString("")
        return "$PREFIX$randomPart"
    }
} 