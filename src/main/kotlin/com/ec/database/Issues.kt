package com.ec.database

import com.ec.database.types.array
import org.jetbrains.exposed.sql.Table

object Issues: Table() {
    val id = varchar("id", 20)
    val title = text("title")
    val message = text("message")
    val timestamp = varchar("timestamp", 20)
    val stack = array("stack", String::class.java)
    val resolved = bool("resolved").default(false)
    override val primaryKey = PrimaryKey(id)
}