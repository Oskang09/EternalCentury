package com.ec.service

import dev.reactant.reactant.core.component.Component
import net.milkbowl.vault.permission.Permission

@Component
class PermissionService: Permission() {
    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun isEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasSuperPermsCompat(): Boolean {
        TODO("Not yet implemented")
    }

    override fun playerHas(world: String?, player: String?, permission: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun playerAdd(world: String?, player: String?, permission: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun playerRemove(world: String?, player: String?, permission: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun groupHas(world: String?, group: String?, permission: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun groupAdd(world: String?, group: String?, permission: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun groupRemove(world: String?, group: String?, permission: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun playerInGroup(world: String?, player: String?, group: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun playerAddGroup(world: String?, player: String?, group: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun playerRemoveGroup(world: String?, player: String?, group: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPlayerGroups(world: String?, player: String?): Array<String> {
        TODO("Not yet implemented")
    }

    override fun getPrimaryGroup(world: String?, player: String?): String {
        TODO("Not yet implemented")
    }

    override fun getGroups(): Array<String> {
        TODO("Not yet implemented")
    }

    override fun hasGroupSupport(): Boolean {
        TODO("Not yet implemented")
    }
}