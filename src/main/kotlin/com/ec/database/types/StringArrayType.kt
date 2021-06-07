package com.ec.database.types

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

fun Table.stringArray(name: String): Column<MutableList<String>>
        = registerColumn(name, StringArrayType())

class StringArrayType: ColumnType() {

    override var nullable: Boolean = true
    override fun sqlType() = "TEXT"

    override fun valueFromDB(value: Any): Any {
        val text = value.toString()
        if (text == "") return mutableListOf<String>()
        return text.split(",").toMutableList()
    }

    override fun valueToDB(value: Any?): Any {
        if (value == null) return ""
        return (value as MutableList<*>).joinToString(",")
    }

}