package com.ec.database

import org.jetbrains.exposed.sql.Table

object Votes: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("player_id", 20)
    val year = integer("year")
    val month = integer("month")
    val day = integer("day")
    val signedAt = long("signedAt")

    override val primaryKey = PrimaryKey(id)
}