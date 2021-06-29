package com.ec.manager.mcmmo

import com.ec.manager.GlobalManager
import com.ec.model.Emoji
import com.ec.util.StringUtil.toComponent
import com.gmail.nossr50.api.ExperienceAPI
import com.gmail.nossr50.datatypes.player.McMMOPlayer
import com.gmail.nossr50.util.player.UserManager
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

@dev.reactant.reactant.core.component.Component
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
            val component = "&f&l玩家 ".toComponent()
                .append(starter.displayName())
                .append(" &f&l发起了挑战 - &e&l${challenge}".toComponent())
                .asComponent()
            val messages = arrayListOf(component)
            messages.addAll(party.onlineMembers.map { member ->
                return@map if (!nearbyMembers.contains(member.name)) {
                    "&c&l${Emoji.CROSS.text} &e&l玩家 ".toComponent()
                        .append(member.displayName())
                        .append(" 还没集合！".toComponent())
                        .asComponent()
                } else {
                    "&c&l${Emoji.CHECK.text} &e&l玩家 ".toComponent()
                        .append(member.displayName())
                        .append(" 准备就绪！".toComponent())
                        .asComponent()
                }
            })

            party.onlineMembers.forEach {
                messages.forEach { c -> it.sendMessage(c) }
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
            val component = "&f&l玩家 ".toComponent()
                .append(starter.displayName())
                .append(" &f&l发起了挑战 - &e&l${challenge}".toComponent())
                .asComponent()
            val messages = arrayListOf(component)
            messages.addAll(party.onlineMembers.map { member ->
                return@map if (!nearbyMembers.contains(member)) {
                    "&c&l${Emoji.CROSS.text} &e&l玩家 ".toComponent()
                        .append(member.displayName())
                        .append(" 还没集合！".toComponent())
                        .asComponent()
                } else {
                    "&c&l${Emoji.CHECK.text} &e&l玩家 ".toComponent()
                        .append(member.displayName())
                        .append(" 准备就绪！".toComponent())
                        .asComponent()
                }
            })

            party.onlineMembers.forEach {
                messages.forEach { c -> it.sendMessage(c) }
            }
            return
        }

        party.onlineMembers.forEach {
            it.teleportAsync(globalManager.serverConfig.teleports[to]!!.location)
        }
    }

}