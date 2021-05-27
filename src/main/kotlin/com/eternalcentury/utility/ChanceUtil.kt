package com.eternalcentury.utility

import java.util.concurrent.ThreadLocalRandom
import java.util.stream.IntStream
import kotlin.math.sqrt

class ChanceUtil {
    companion object {
        private fun random(bound: Int): Int {
            return ThreadLocalRandom.current().nextInt(bound)
        }

        // Trigger based on random value
        fun defaultChance(chance: Int): Boolean {
            return random(100) < chance
        }

        // Must over [n] times only will trigger
        fun increasingChance(base: Int, times: Int): Boolean {
            val increment = sqrt(base.toDouble()) * times
            val pseudoValue = base + increment
            return pseudoValue + random(pseudoValue.toInt()) >= 100
        }

        // Higher [n] times, higher chance to trigger
        fun trueChance(base: Int, times: Int): Boolean {
            val trueValue = IntStream.range(1, times).map { random(base) }.sum()
            return base + trueValue + random(base * times) >= 100
        }
    }
}