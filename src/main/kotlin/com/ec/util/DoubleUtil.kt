package com.ec.util

import kotlin.math.pow
import kotlin.math.roundToInt

object DoubleUtil {

    fun Double.roundTo(numFractionDigits: Int): Double {
        val factor = (10.0).pow(numFractionDigits.toDouble())
        return (this * factor).roundToInt() / factor
    }

}