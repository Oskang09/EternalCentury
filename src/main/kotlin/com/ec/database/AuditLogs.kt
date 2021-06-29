package com.ec.database

import com.ec.api.ActionLog
import com.ec.database.types.json
import org.jetbrains.exposed.sql.Table

object AuditLogs: Table() {
    val id = varchar("id", 20)
    val adminId = varchar("admin_id", 20)
    val action = varchar("action", 30)
    val log = json("log", ActionLog::class.java)
    val actionAt = long("action_at")

    override val primaryKey = PrimaryKey(id)
}

data class AuditLog(val action: String = "NO_ACTION_DEFINE")