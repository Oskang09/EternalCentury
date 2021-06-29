package com.ec.database

import com.ec.config.RewardConfig
import com.ec.database.types.array
import org.jetbrains.exposed.sql.Table

object Announcements: Table() {
    val id = varchar("id", 20)
    val title = varchar("title", 256)
    val content = varchar("content", 1024)
    val rewards = array("rewards", RewardConfig::class.java)
    val createdAt = long("created_at")
    val isExpired = bool("is_expired")

    override val primaryKey = PrimaryKey(id)
}