package com.ec.database.types

import com.ec.database.enums.AdminStatus
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

fun <T : Enum<T>> Table.enum(name: String): Column<T>
        = registerColumn(name, EnumType<T>())

class EnumType<T : Enum<T>>: ColumnType() {

    override var nullable: Boolean = true
    override fun sqlType() = "TEXT"

    override fun valueFromDB(value: Any): Any {
        val text = value.toString()
        if (text == "") return ""
        return enumValueOf<Enum<T>>(text)
    }

    override fun valueToDB(value: Any?): Any {
        if (value == null) return ""
        return value.toString()
    }

}