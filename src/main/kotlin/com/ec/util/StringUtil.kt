package com.ec.util

object StringUtil {

    fun String.colorize(): String {
        return this.replace("&", "§")
    }

    fun List<String>.colorize(): List<String> {
        return this.map {
            return@map it.replace("&", "§")
        }
    }

}