package com.ec.database

import org.jetbrains.exposed.sql.Table

object ZombieFights: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("playerId", 20)
    val damage = double("damage")
    val rank = integer("rank")
    val year = integer("year")
    val month = integer("month")
    val day = integer("day")

    override val primaryKey = PrimaryKey(id)
}