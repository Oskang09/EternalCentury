package com.ec.util

import com.ec.database.model.ChatType

object ModelUtil {

    fun ChatType.toDisplay(): String {
        return when (this) {
            ChatType.GLOBAL -> "全球综合"
            ChatType.PVPVE -> "附魔战斗"
            ChatType.SURVIVAL -> "资源生存"
            ChatType.MCMMO -> "角色达人"
            ChatType.REDSTONE -> "红石机关"
            ChatType.PARTY -> "队伍讨论"
        }
    }

}