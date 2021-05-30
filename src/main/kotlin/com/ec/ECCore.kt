package com.ec

import com.ec.service.ChatService
import com.ec.service.EconomyService
import com.ec.service.PermissionService
import dev.reactant.reactant.core.ReactantPlugin
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

@ReactantPlugin(["com.ec"])
class ECCore: JavaPlugin() {

    companion object {
        lateinit var instance: JavaPlugin private set
    }

    override fun onEnable() {
        instance = this

        val service = server.servicesManager
        val perms = PermissionService()
        service.register(Permission::class.java, perms, this, ServicePriority.Highest)
        service.register(Economy::class.java, EconomyService(), this, ServicePriority.Highest)
        service.register(Chat::class.java, ChatService(perms), this, ServicePriority.Highest)
    }

}