package com.ec.util

import com.ec.database.model.ChatType

object ModelUtil {

    fun ChatType.toDisplay(): String {
        return when (this) {
            ChatType.GLOBAL -> "全球综合"
            ChatType.PARTY -> "队伍讨论"
            ChatType.ANNOUNCEMENT -> "实时广播"
        }
    }

}