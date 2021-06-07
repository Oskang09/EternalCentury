package com.ec.database

import org.jetbrains.exposed.sql.Table

object Titles: Table()  {
    val id = varchar("id", 20)
    val playerId = varchar("player_id", 20)
    val titleId = varchar("title_id", 50)
    val unlockedAt = long("unlocked_at")

    override val primaryKey = PrimaryKey(id)
}