package com.ec.util

import com.ec.logger.Logger
import java.text.SimpleDateFormat
import java.util.*

object StringUtil {
    private val UNIQUE_ID_FORMAT: SimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmssSS")


    fun String.colorize(): String {
        return this.replace("&", "§")
    }

    fun List<String>.colorize(): List<String> {
        return this.map {
            return@map it.replace("&", "§")
        }
    }

    fun String.generateUniqueID(): String {
        val id = UNIQUE_ID_FORMAT.format(Date())
        Thread.sleep(1)
        return id
    }

}