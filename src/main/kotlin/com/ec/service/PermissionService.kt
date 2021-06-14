package com.ec.service

import com.ec.ECCore
import com.ec.database.Players
import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment
import java.util.*

@Component
class PermissionService: Permission() {

    private val playersDefaultPermissions = listOf(
        "mcmmo.commands.defaults",
        "mcmmo.ability.*",
        "residence.create",
        "residence.permisiononerror",
        "residence.command.message.enter",
        "residence.command.message.leave",
        "residence.command.message.enter.remove",
        "residence.command.message.leave.remove"
    )

    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    fun injectPermission(player: Player) {
        val ecPlayer = globalManager.players.getByPlayer(player)
        val permissions = ecPlayer.database[Players.permissions]
        permissions.addAll(playersDefaultPermissions)
        permissions.forEach {
            player.addAttachment(ECCore.instance, it, true)
        }
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
        globalManager.players.refreshPlayerIfOnline(UUID.fromString(ecPlayer[Players.uuid]!!)) {
            it.addAttachment(ECCore.instance, permission, true)
        }
        return ecPlayer[Players.permissions].contains(permission) || playersDefaultPermissions.contains(permission)
    }

    override fun playerRemove(world: String?, player: String, permission: String): Boolean {
        val ecPlayer = globalManager.players.getByPlayerName(player) ?: return false
        globalManager.players.refreshPlayerIfOnline(UUID.fromString(ecPlayer[Players.uuid]!!)) {
            it.addAttachment(ECCore.instance, permission, false)
        }
        return ecPlayer[Players.permissions].contains(permission) || playersDefaultPermissions.contains(permission)
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