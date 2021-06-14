package com.ec.service

import com.ec.database.model.ChatType
import com.ec.util.ModelUtil.toDisplay
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.core.component.Component
import net.citizensnpcs.api.npc.NPC
import org.bukkit.World
import org.bukkit.entity.Player

@Component
class MessageService {

    fun system(message: String): String {
        return "&b[&5系统&b] &r$message".colorize()
    }

    fun private(from: Player,  message: String): String {
        return "&b[&9私讯&b] &r${from.displayName}: &f".colorize() + message
    }

    fun npc(npc: NPC, message: String): String {
        return "${npc.fullName} : &f$message".colorize()
    }

    fun playerChat(player: Player, chatType: ChatType, message: String): String {
        return "&f[&a${chatType.toDisplay()}&f] &r${player.displayName} : &f".colorize() + message
    }

}