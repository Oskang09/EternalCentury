package com.ec.util

import java.util.concurrent.ThreadLocalRandom

object RandomUtil {
    private fun random(bound: Int): Int {
        return ThreadLocalRandom.current().nextInt(bound)
    }

    fun randomInteger(bound: Int): Int {
        return random(bound)
    }
}