package com.eternalcentury.service

import dev.reactant.reactant.core.component.Component
import net.milkbowl.vault.chat.Chat

@Component
class ChatService(
    private val perms: PermissionService
): Chat(perms) {
    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun isEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPlayerPrefix(world: String?, player: String?): String {
        TODO("Not yet implemented")
    }

    override fun setPlayerPrefix(world: String?, player: String?, prefix: String?) {
        TODO("Not yet implemented")
    }

    override fun getPlayerSuffix(world: String?, player: String?): String {
        TODO("Not yet implemented")
    }

    override fun setPlayerSuffix(world: String?, player: String?, suffix: String?) {
        TODO("Not yet implemented")
    }

    override fun getGroupPrefix(world: String?, group: String?): String {
        TODO("Not yet implemented")
    }

    override fun setGroupPrefix(world: String?, group: String?, prefix: String?) {
        TODO("Not yet implemented")
    }

    override fun getGroupSuffix(world: String?, group: String?): String {
        TODO("Not yet implemented")
    }

    override fun setGroupSuffix(world: String?, group: String?, suffix: String?) {
        TODO("Not yet implemented")
    }

    override fun getPlayerInfoInteger(world: String?, player: String?, node: String?, defaultValue: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setPlayerInfoInteger(world: String?, player: String?, node: String?, value: Int) {
        TODO("Not yet implemented")
    }

    override fun getGroupInfoInteger(world: String?, group: String?, node: String?, defaultValue: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setGroupInfoInteger(world: String?, group: String?, node: String?, value: Int) {
        TODO("Not yet implemented")
    }

    override fun getPlayerInfoDouble(world: String?, player: String?, node: String?, defaultValue: Double): Double {
        TODO("Not yet implemented")
    }

    override fun setPlayerInfoDouble(world: String?, player: String?, node: String?, value: Double) {
        TODO("Not yet implemented")
    }

    override fun getGroupInfoDouble(world: String?, group: String?, node: String?, defaultValue: Double): Double {
        TODO("Not yet implemented")
    }

    override fun setGroupInfoDouble(world: String?, group: String?, node: String?, value: Double) {
        TODO("Not yet implemented")
    }

    override fun getPlayerInfoBoolean(world: String?, player: String?, node: String?, defaultValue: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun setPlayerInfoBoolean(world: String?, player: String?, node: String?, value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getGroupInfoBoolean(world: String?, group: String?, node: String?, defaultValue: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun setGroupInfoBoolean(world: String?, group: String?, node: String?, value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getPlayerInfoString(world: String?, player: String?, node: String?, defaultValue: String?): String {
        TODO("Not yet implemented")
    }

    override fun setPlayerInfoString(world: String?, player: String?, node: String?, value: String?) {
        TODO("Not yet implemented")
    }

    override fun getGroupInfoString(world: String?, group: String?, node: String?, defaultValue: String?): String {
        TODO("Not yet implemented")
    }

    override fun setGroupInfoString(world: String?, group: String?, node: String?, value: String?) {
        TODO("Not yet implemented")
    }
}