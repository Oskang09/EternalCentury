package com.ec.util

import java.util.*

object RomanUtil {

    private val romanMaps: TreeMap<Int, String> = TreeMap()

    init {
        romanMaps[1000] = "M"
        romanMaps[900] = "CM"
        romanMaps[500] = "D"
        romanMaps[400] = "CD"
        romanMaps[100] = "C"
        romanMaps[90] = "XC"
        romanMaps[50] = "L"
        romanMaps[40] = "XL"
        romanMaps[10] = "X"
        romanMaps[9] = "IX"
        romanMaps[5] = "V"
        romanMaps[4] = "IV"
        romanMaps[1] = "I"
    }

    fun Int.toRoman(): String {
        val key = romanMaps.floorKey(this)
        if (this == key) {
            return romanMaps[this]!!
        }
        return romanMaps[key] + (this - key).toRoman()
    }

}