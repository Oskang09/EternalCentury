package com.eternalcentury.utility

import java.util.concurrent.ThreadLocalRandom

class RandomUtil {
    companion object {
        private fun random(bound: Int): Int {
            return ThreadLocalRandom.current().nextInt(bound)
        }

        fun randomInteger(bound: Int): Int {
            return random(bound)
        }
    }
}