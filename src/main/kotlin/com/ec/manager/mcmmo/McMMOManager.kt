package com.ec.manager.mcmmo

import com.ec.manager.GlobalManager
import com.ec.model.Emoji
import com.ec.util.StringUtil.colorize
import com.gmail.nossr50.api.ExperienceAPI
import com.gmail.nossr50.datatypes.player.McMMOPlayer
import com.gmail.nossr50.util.player.UserManager
import dev.reactant.reactant.core.component.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@Component
class McMMOManager {

    private lateinit var globalManager: GlobalManager
    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    fun getPlayer(player: Player): McMMOPlayer? {
        return UserManager.getPlayer(player)
    }

    fun getOverallRank(player: Player): Int {
        return try {
            ExperienceAPI.getPlayerRankOverall(player.uniqueId)
        } catch (e: Throwable) {
            0
        }
    }

    fun getRank(player: Player, skill: String): Int {
        return ExperienceAPI.getPlayerRankSkill(player.uniqueId, skill)
    }

    fun getPlayerParty(player: Player): List<Player> {
        val mcmmoPlayer = UserManager.getPlayer(player)
        if (!mcmmoPlayer.inParty()) {
            player.sendMessage(globalManager.message.system("您目前不在任何队伍。"))
            return listOf()
        }

        val party = mcmmoPlayer.party
        return party.onlineMembers

    }

    fun partyIsNearby(starter: Player, challenge: String): Boolean {
        val mcmmoPlayer = UserManager.getPlayer(starter)
        if (!mcmmoPlayer.inParty()) {
            starter.sendMessage(globalManager.message.system("您目前不在任何队伍。"))
            return false
        }

        val party = mcmmoPlayer.party
        val nearbyMembers = party.getNearMembers(mcmmoPlayer).map { it.name }
        if (nearbyMembers.size != party.members.size - 1) {
            val messages = arrayListOf("&f&l玩家 ${starter.name} &f&l发起了挑战 - $challenge")
            messages.addAll(party.members.map { (_, member) ->
                return@map if (member != starter.name && !nearbyMembers.contains(member)) {
                    "&c&l${Emoji.CROSS.text} &e&l玩家 $member 还没集合！"
                } else {
                    "&a&l${Emoji.CHECK.text} &e&l玩家 $member 准备就绪！"
                }
            })

            party.onlineMembers.forEach {
                it.sendMessage(
                    messages.map { msg -> globalManager.message.system(msg) }
                        .colorize()
                        .toTypedArray()
                )
            }
            return false
        }
        return true
    }

    fun partyCommands(starter: Player, commands: List<String>) {
        val mcmmoPlayer = UserManager.getPlayer(starter)
        if (!mcmmoPlayer.inParty()) {
            starter.sendMessage(globalManager.message.system("您目前不在任何队伍。"))
            return
        }

        val party = mcmmoPlayer.party
        val onlineMembers = party.onlineMembers
        commands.forEach { cmd ->
            onlineMembers.forEach {
                it.performCommand(cmd)
            }
        }
    }

    fun partyTeleport(starter: Player, challenge: String, to: String) {
        val mcmmoPlayer = UserManager.getPlayer(starter)
        if (!mcmmoPlayer.inParty()) {
            starter.sendMessage(globalManager.message.system("您目前不在任何队伍。"))
            return
        }

        val party = mcmmoPlayer.party
        val nearbyMembers = party.getNearMembers(mcmmoPlayer)
        if (nearbyMembers.size != party.onlineMembers.size) {
            val messages = arrayListOf(globalManager.message.system("&f&l玩家 ${starter.displayName} &f&l发起了挑战 - &e&l${challenge}"))
            messages.addAll(party.onlineMembers.map { member ->
                return@map if (!nearbyMembers.contains(member)) {
                    "&c&l${Emoji.CROSS.text} &e&l玩家 ${member.displayName} 还没集合！"
                } else {
                    "&e&l玩家 ${member.displayName} 准备就绪！"
                }
            })

            party.onlineMembers.forEach {
                it.sendMessage(messages.toTypedArray())
            }
            return
        }

        party.onlineMembers.forEach {
            it.teleport(globalManager.serverConfig.teleports[to]!!.location)
        }
    }

}