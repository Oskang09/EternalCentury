package com.ec.database

import org.jetbrains.exposed.sql.Table

object VoteRewards: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("player_id", 20)
    val year = integer("year")
    val month = integer("month")
    val reward = integer("reward")
    val signedAt = long("signed_at")

    override val primaryKey = PrimaryKey(id)
}