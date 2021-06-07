package com.ec.service

import com.ec.database.Players
import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import net.milkbowl.vault.permission.Permission

@Component
class PermissionService: Permission() {

    private lateinit var globalManager: GlobalManager
    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
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
        return ecPlayer[Players.permissions].contains(permission)
    }

    override fun playerAdd(world: String?, player: String, permission: String): Boolean {
        val ecPlayer = globalManager.players.getByPlayerName(player) ?: return false
        return ecPlayer[Players.permissions].contains(permission)
    }

    override fun playerRemove(world: String?, player: String, permission: String): Boolean {
        val ecPlayer = globalManager.players.getByPlayerName(player) ?: return false
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