package com.ec.database

import com.ec.config.RewardConfig
import com.ec.database.types.array
import com.ec.database.types.minecraft
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.Table

object Mails: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("player_id", 20)
    val announcementId = varchar("announcement_id", 20).nullable()
    val title = varchar("title", 256)
    val content = varchar("content", 1024)
    val item = array("item", ItemStack::class.java)
    val rewards = array("rewards", RewardConfig::class.java)
    val isRead = bool("isRead")
    val createdAt = long("createdAt")

    override val primaryKey = PrimaryKey(id)
}