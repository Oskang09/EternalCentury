package com.ec

import com.ec.database.*
import com.ec.service.EconomyService
import com.ec.service.PermissionService
import dev.reactant.reactant.core.ReactantPlugin
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection

@ReactantPlugin(["com.ec"])
class ECCore: JavaPlugin() {

    companion object {
        lateinit var instance: JavaPlugin private set
    }

    override fun onEnable() {
        instance = this

        Database.connect("jdbc:sqlite:${File(dataFolder.absolutePath, "../server-data/sqlite.db")}", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            SchemaUtils.createMissingTablesAndColumns(Economies, Issues, Players, Points, Titles)
        }

        val service = server.servicesManager
        val perms = PermissionService()
        service.register(Permission::class.java, perms, this, ServicePriority.Highest)
        service.register(Economy::class.java, EconomyService(), this, ServicePriority.Highest)
    }

}