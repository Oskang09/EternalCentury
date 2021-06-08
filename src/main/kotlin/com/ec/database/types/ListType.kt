package com.ec.database.types

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

fun <T : Any>  Table.array(name: String, clazz: Class<T>, jsonMapper: ObjectMapper = ObjectMapper()): Column<MutableList<T>>
        = registerColumn(name, ArrayType(clazz, jsonMapper))

class ArrayType<out T: Any>(private val clazz: Class<T>, private val mapper: ObjectMapper): ColumnType() {

    override var nullable: Boolean = true
    override fun sqlType() = "TEXT"

    override fun valueFromDB(value: Any): Any {
        val text = value.toString()
        if (text == "") return mutableListOf<String>()
        val javaType: CollectionType = mapper.typeFactory.constructCollectionType(MutableList::class.java, clazz)
        return mapper.readValue(text, javaType)
    }

    override fun valueToDB(value: Any?): Any {
        if (value == null) return ""
        return mapper.writeValueAsString(value)
    }

}