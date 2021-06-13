package com.ec.database

import com.ec.config.RewardConfig
import com.ec.database.types.array
import org.jetbrains.exposed.sql.Table

object Mails: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("player_id", 20)
    val announcementId = varchar("announcement_id", 20).nullable()
    val title = varchar("title", 256)
    val content = varchar("content", 1024)
    val rewards = array("rewards", RewardConfig::class.java).nullable()
    val isRead = bool("isRead")
    val createdAt = long("createdAt")

    override val primaryKey = PrimaryKey(id)
}