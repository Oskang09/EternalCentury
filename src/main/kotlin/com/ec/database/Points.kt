package com.ec.database

import com.ec.database.types.enum
import com.ec.database.model.point.PointType
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table

object Points: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("player_id", 20)
    val type = enum<PointType>("type")
    val point = varchar("point", 50)
    val balance = double("balance")
    val actionAt = long("action_at")

    override val primaryKey = PrimaryKey(id)
}