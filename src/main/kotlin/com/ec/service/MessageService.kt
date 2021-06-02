package com.ec.service

import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.core.component.Component
import net.citizensnpcs.api.npc.NPC
import org.bukkit.World
import org.bukkit.entity.Player

@Component
class MessageService {

    private val worldMapper = mutableMapOf<String, String>(
        "world" to "资源世界"
    )

    private fun transformWorld(world: World): String {
        return worldMapper[world.name]!!
    }

    fun system(message: String): String {
        return "&b[&5系统&b] &r$message".colorize()
    }

    fun npc(npc: NPC, message: String): String {
        return "&f[&a${transformWorld(npc.entity.world)}&f] &r${npc.fullName} : &f$message".colorize()
    }

    fun player(player: Player, message: String): String {
        return "&f[&a${transformWorld(player.world)}&f] &r${player.displayName} : &f$message".colorize()
    }

}