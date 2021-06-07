package com.ec.database

import com.ec.database.types.enum
import com.ec.database.model.economy.EconomyType
import org.jetbrains.exposed.sql.Table

object Economies: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("player_id", 20)
    val type = enum<EconomyType>("type")
    val balance = double("balance")
    val actionAt = long("action_at")

    override val primaryKey = PrimaryKey(id)
}