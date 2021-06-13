package com.ec

import com.ec.database.*
import com.ec.service.EconomyService
import com.ec.service.PermissionService
import dev.reactant.reactant.core.ReactantPlugin
import kotlinx.coroutines.selects.select
import me.oska.UniversalGUI
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
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
        const val VERSION = "0.0.1"
        lateinit var instance: JavaPlugin private set
    }

    override fun onEnable() {
        instance = this

        Database.connect("jdbc:sqlite:${File(dataFolder.absolutePath, "../EternalCentury/sqlite.db")}", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Announcements, Mails, Malls, MallHistories, Economies, Issues,
                Players, Points, Titles, Votes, VoteRewards
            )

            exec("SELECT 1")
        }

        val service = server.servicesManager
        service.register(Permission::class.java, PermissionService(), this, ServicePriority.Highest)
        service.register(Economy::class.java, EconomyService(), this, ServicePriority.Highest)
    }

}