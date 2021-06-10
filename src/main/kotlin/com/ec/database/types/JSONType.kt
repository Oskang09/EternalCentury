package com.ec.database.types

import com.fasterxml.jackson.databind.ObjectMapper
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

fun <T : Any> Table.json(
    name: String,
    clazz: Class<T>,
    jsonMapper: ObjectMapper = ObjectMapper()
): Column<T>
        = registerColumn(name, JSONType(clazz, jsonMapper))

class JSONType<out T: Any>(private val clazz: Class<T>, private val mapper: ObjectMapper): ColumnType() {

    override var nullable: Boolean = false
    override fun sqlType() = "TEXT"

    override fun valueFromDB(value: Any): Any {
        val text = value.toString()
        if (text == "") return clazz.newInstance()
        return mapper.readValue(text, clazz)
    }

    override fun valueToDB(value: Any?): Any? {
        if (value == null) return ""
        return mapper.writeValueAsString(value)
    }

}