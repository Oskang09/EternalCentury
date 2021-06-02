package com.ec.logger

fun interface LogTracker {
    @Throws(Exception::class)
    fun track()
}
