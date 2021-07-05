package com.ec.service

import com.ec.ECCore
import com.ec.database.Players
import com.ec.manager.GlobalManager
import dev.reactant.reactant.core.component.Component
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.permissions.PermissionAttachment
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

@Component
class PermissionService: Permission() {

    private val playersDefaultPermissions = listOf(
        "mcmmo.commands.defaults",
        "mcmmo.ability.*",
        "mcmmo.skills.*",
        "plots.auto",
        "plots.claim",
        "plots.set.flag",
        "plots.set.home",
        "plots.flag",
        "plots.flag.add",
        "plots.flag.remove",
        "plots.biome",
        "plots.music",
        "plots.confirm",
        "plots.add",
        "plots.trust",
        "plots.add.*",
        "plots.trust.*",
        "plots.remove",
        "plots.deny",
        "plots.kick",
        "plots.like",
        "plots.dislike",
        "plots.rate",
        "plots.home",
        "plots.auto",
        "plots.delete",
        "plots.visit",
        "playerparticles.particles.max.3",
        "playerparticles.groups.max.3",
    )

    private lateinit var globalManager: GlobalManager
    private val permissionAttachment: MutableMap<String, PermissionAttachment> = mutableMapOf()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.events {
            PlayerQuitEvent::class
                .observable(true, EventPriority.HIGHEST)
                .subscribe {
                    permissionAttachment.remove(it.player.uniqueId.toString())
                }
        }
    }

    fun injectPermission(player: Player) {
        val ecPlayer = globalManager.players.getByPlayer(player)
        val permissions = ecPlayer.database[Players.permissions]
        permissions.addAll(playersDefaultPermissions)

        val attachment = permissionAttachment[player.uniqueId.toString()]
        if (attachment != null) {
            player.removeAttachment(attachment)
        }

        val newAttachment = player.addAttachment(ECCore.instance)
        permissionAttachment[player.uniqueId.toString()] = newAttachment

        val plotLimit = ecPlayer.database[Players.plotLimit]
        permissions.add("plots.plot.$plotLimit")
        permissions.forEach {
            newAttachment.setPermission(it, true)
        }

        player.recalculatePermissions()
    }

    override fun getName(): String {
        return "Permission"
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun hasSuperPermsCompat(): Boolean {
        return true
    }

    override fun playerHas(world: String?, player: String, permission: String): Boolean {
        val ecPlayer = globalManager.players.getByPlayerName(player) ?: return false
        return ecPlayer[Players.permissions].contains(permission) || playersDefaultPermissions.contains(permission)
    }

    override fun playerAdd(world: String?, player: String, permission: String): Boolean {
        val ecPlayer = globalManager.players.getByPlayerName(player) ?: return false
        transaction {
            val list = ecPlayer[Players.permissions]
            list.add(permission)

            Players.update({ Players.id eq ecPlayer[Players.id] }) {
                it[permissions] = list
            }
        }

        globalManager.players.refreshPlayerIfOnline(UUID.fromString(ecPlayer[Players.uuid]!!)) {
            globalManager.permission.injectPermission(it)
        }

        return ecPlayer[Players.permissions].contains(permission)
    }

    override fun playerRemove(world: String?, player: String, permission: String): Boolean {
        val ecPlayer = globalManager.players.getByPlayerName(player) ?: return false

        transaction {
            val list = ecPlayer[Players.permissions]
            list.remove(permission)

            Players.update({ Players.id eq ecPlayer[Players.id] }) {
                it[permissions] = list
            }
        }

        globalManager.players.refreshPlayerIfOnline(UUID.fromString(ecPlayer[Players.uuid]!!)) {
            globalManager.permission.injectPermission(it)
        }

        return ecPlayer[Players.permissions].contains(permission)
    }

    override fun groupHas(world: String?, group: String?, permission: String?): Boolean {
        return false
    }

    override fun groupAdd(world: String?, group: String?, permission: String?): Boolean {
        return false
    }

    override fun groupRemove(world: String?, group: String?, permission: String?): Boolean {
        return false
    }

    override fun playerInGroup(world: String?, player: String?, group: String?): Boolean {
        return false
    }

    override fun playerAddGroup(world: String?, player: String?, group: String?): Boolean {
        return false
    }

    override fun playerRemoveGroup(world: String?, player: String?, group: String?): Boolean {
        return false
    }

    override fun getPlayerGroups(world: String?, player: String?): Array<String> {
        return arrayOf()
    }

    override fun getPrimaryGroup(world: String?, player: String?): String {
        return ""
    }

    override fun getGroups(): Array<String> {
        return arrayOf()
    }

    override fun hasGroupSupport(): Boolean {
        return false
    }
}