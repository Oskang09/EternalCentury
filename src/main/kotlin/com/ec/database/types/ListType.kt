package com.ec.database.types

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import kotlin.reflect.typeOf

fun <T : Any> Table.array(name: String, clazz: Class<T>, jsonMapper: ObjectMapper = ObjectMapper()): Column<MutableList<T>>
        = registerColumn(name, ArrayType(clazz, jsonMapper))

class ArrayType<out T: Any>(private val clazz: Class<T>, private val mapper: ObjectMapper): ColumnType() {

    override var nullable: Boolean = false
    override fun sqlType() = "TEXT"

    override fun valueFromDB(value: Any): Any {
        val text = if (value is String) value.toString() else mapper.writeValueAsString(value)
        if (text == "" || text == "[]") return mutableListOf<T>()
        val javaType: CollectionType = mapper.typeFactory.constructCollectionType(List::class.java, clazz)
        return mapper.readValue(text, javaType)
    }

    override fun valueToDB(value: Any?): Any {
        if (value == null) return ""
        return mapper.writeValueAsString(value)
    }

}