package com.ec.database

import com.ec.database.enums.PlayerStatus
import com.ec.database.types.array
import org.jetbrains.exposed.sql.Table

object Players: Table() {
    val id = varchar("id", 20)
    val uuid = varchar("uuid", 36).uniqueIndex().nullable()
    val playerName = varchar("player_name", 20).uniqueIndex()
    var discordTag = varchar("discord_tag", 50).uniqueIndex()
    val createdAt = long("created_at")
    val lastOnlineAt = long("last_online")
    val currentTitle = varchar("current_title", 50).default("")
    val enchantmentRandomSeed = integer("enchantment_seed")
    val skinLimit = integer("skin_limit").default(1)
    val plotLimit = integer("plot_limit").default(1)
    val auctionLimit = integer("auction_limit").default(1)
    val skins = array("skins", String::class.java)
    val permissions = array("permissions", String::class.java)
    // list of players uuid
    val blockedTeleport = array("blocked_teleport", String::class.java)
    // list of players uuid
    val ignoredPlayers = array("ignored_players", String::class.java)
    val status = enumerationByName("status", 20, PlayerStatus::class).default(PlayerStatus.ACTIVE)
    val reason = text("reason").default("")

    override val primaryKey = PrimaryKey(id)
}