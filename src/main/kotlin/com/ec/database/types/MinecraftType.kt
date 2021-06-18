package com.ec.database.types

import com.fasterxml.jackson.databind.ObjectMapper
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

fun <T : ConfigurationSerializable> Table.minecraft(name: String, clazz: Class<T>, jsonMapper: ObjectMapper = ObjectMapper()): Column<T>
        = registerColumn(name, MinecraftType(clazz, jsonMapper))

class MinecraftType<out T: ConfigurationSerializable> (private val clazz: Class<T>, private val mapper: ObjectMapper): ColumnType() {

    override var nullable: Boolean = false
    override fun sqlType() = "TEXT"

    override fun valueFromDB(value: Any): Any {
        val text = value.toString()
        if (text == "") return clazz.getDeclaredConstructor().newInstance()
        val javaType = mapper.typeFactory.constructMapType(MutableMap::class.java, String::class.java, Any::class.java)
        val map: MutableMap<String, Any> = mapper.readValue(text, javaType)
        val deserialize = clazz.getMethod("deserialize", Map::class.java)
        return deserialize.invoke(null, map)
    }

    override fun valueToDB(value: Any?): Any? {
        if (value == null) return ""
        val serializable = value as ConfigurationSerializable
        return mapper.writeValueAsString(serializable.serialize())
    }

}