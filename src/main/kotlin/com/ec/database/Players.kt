package com.ec.database

import com.ec.database.model.ChatType
import com.ec.database.model.economy.EconomyInfo
import com.ec.database.model.point.PointInfo
import com.ec.database.types.array
import com.ec.database.types.json
import org.jetbrains.exposed.sql.Table

object Players: Table() {
    val id = varchar("id", 20)
    val uuid = varchar("uuid", 36).uniqueIndex().nullable()
    val playerName = varchar("player_name", 20).uniqueIndex()
    var discordTag = varchar("discord_tag", 50).uniqueIndex()
    val playTimes = long("play_times")
    val createdAt = long("created_at")
    val lastOnlineAt = long("last_online")
    val currentTitle = varchar("current_title", 50)
    val balance = json("balance", EconomyInfo::class.java)
    val enchantmentRandomSeed = integer("enchantment_seed")
    val points = json("points", PointInfo::class.java)
    val permissions = array("permissions", String::class.java)
    val channels = array("channels", ChatType::class.java)
    val blockedTeleport = array("blocked_teleport", String::class.java)
    override val primaryKey = PrimaryKey(id)
}