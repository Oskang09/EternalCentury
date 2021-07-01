package com.ec.database

import com.ec.config.RewardConfig
import com.ec.database.types.array
import org.jetbrains.exposed.sql.Table

object Announcements: Table() {
    val id = varchar("id", 20)
    val title = varchar("title", 256)
    val content = array("content", String::class.java)
    val rewards = array("rewards", RewardConfig::class.java)
    val createdAt = long("createdAt")
    val isExpired = bool("isExpired")

    override val primaryKey = PrimaryKey(id)
}