package com.ec.util

import com.ec.database.Admins
import com.ec.database.enums.ChatType
import org.jetbrains.exposed.sql.ResultRow

object ModelUtil {

    fun ChatType.toDisplay(): String {
        return when (this) {
            ChatType.GLOBAL -> "全球综合"
            ChatType.PARTY -> "队伍讨论"
            ChatType.ANNOUNCEMENT -> "实时广播"
        }
    }

    fun ResultRow.toJSON(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        this.fieldIndex.forEach { (key, _) ->
            when (key) { Admins.apiKey -> return@forEach }

            map[key.toString().split(".").last()] = this[key]
        }
        return map.toMap()
    }

    fun List<ResultRow>.toJSON(): List<Map<String, Any?>> {
        return this.map { r -> r.toJSON() }
    }

}