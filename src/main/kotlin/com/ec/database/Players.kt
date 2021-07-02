package com.ec.database

import com.ec.database.enums.PlayerStatus
import com.ec.database.types.array
import org.jetbrains.exposed.sql.Table

object Players: Table() {
    val id = varchar("id", 20)
    val uuid = varchar("uuid", 36).uniqueIndex().nullable()
    val playerName = varchar("playerName", 20).uniqueIndex()
    val discordId = varchar("discordId", 50).uniqueIndex().nullable()
    var discordTag = varchar("discordTag", 50).uniqueIndex()
    val createdAt = long("createdAt")
    val lastOnlineAt = long("lastOnline")
    val lastVerifyIPAddress = varchar("lastVerifyIpAddress", 20).default("")
    val lastVerifiedAt = long("lastVerifiedAt").default(0)
    val currentTitle = varchar("currentTitle", 50).default("")
    val enchantmentRandomSeed = integer("enchantmentSeed")
    val skinLimit = integer("skinLimit").default(1)
    val plotLimit = integer("plotLimit").default(1)
    val auctionLimit = integer("auctionLimit").default(1)
    val homeLimit = integer("homeLimit").default(1)
    val skins = array("skins", String::class.java)
    val permissions = array("permissions", String::class.java)
    // list of players uuid
    val blockedTeleport = array("blockedTeleport", String::class.java)
    // list of players uuid
    val ignoredPlayers = array("ignoredPlayers", String::class.java)
    val status = enumerationByName("status", 20, PlayerStatus::class).default(PlayerStatus.ACTIVE)
    val reason = text("reason").default("")

    override val primaryKey = PrimaryKey(id)
}