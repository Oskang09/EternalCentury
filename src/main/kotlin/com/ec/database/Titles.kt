package com.ec.database

import org.jetbrains.exposed.sql.Table

object Titles: Table()  {
    val id = varchar("id", 20)
    val playerId = varchar("playerId", 20)
    val titleId = varchar("titleId", 50)
    val unlockedAt = long("unlockedAt")

    override val primaryKey = PrimaryKey(id)
}