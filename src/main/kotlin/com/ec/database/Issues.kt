package com.ec.database

import com.ec.database.types.stringArray
import org.jetbrains.exposed.sql.Table

object Issues: Table() {
    val id = varchar("id", 20)
    val title = text("title")
    val message = text("message")
    val timestamp = varchar("timestamp", 20)
    val stack = stringArray("stack")
    override val primaryKey = PrimaryKey(id)
}