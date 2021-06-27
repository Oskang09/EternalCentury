package com.ec.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object InstantUtil {

    private val readableDateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
    private val systemDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val zone = ZoneId.of("Asia/Kuala_Lumpur")

    fun Long.toMalaysiaTime(): ZonedDateTime {
        return Instant.ofEpochSecond(this).atZone(zone)
    }

    fun Long.toMalaysiaSystemDate(): String {
        return this.toMalaysiaTime().format(systemDateFormat)
    }

    fun Long.toMalaysiaReadableTime(): String {
        return this.toMalaysiaTime().format(readableDateFormat)
    }


}