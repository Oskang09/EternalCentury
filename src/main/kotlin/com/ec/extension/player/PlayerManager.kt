package com.ec.extension.player

import com.ec.database.Players
import com.ec.extension.GlobalManager
import com.ec.logger.Logger
import com.ec.model.player.ECPlayer
import com.ec.model.player.ECPlayerState
import dev.reactant.reactant.core.component.Component
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class PlayerManager {
    private val players: MutableMap<UUID, ECPlayer> = HashMap()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

         globalManager.events {

             AsyncPlayerPreLoginEvent::class
                .observable(true, EventPriority.HIGHEST)
                .subscribe {
                    val player = transaction {
                        Players.select { Players.playerName eq it.name }.singleOrNull()
                    }
                    if (player == null) {
                        it.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "not registered")
                        return@subscribe
                    }
                    it.allow()
                }

             PlayerInteractEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .filter { globalManager.players.getByPlayer(it.player).state != ECPlayerState.AUTHENTICATED }
                 .subscribe {
                     it.isCancelled = true
                 }

             InventoryClickEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .filter { globalManager.players.getByPlayer(it.whoClicked as Player).state != ECPlayerState.AUTHENTICATED }
                 .subscribe {
                     it.isCancelled = true
                 }

             PlayerDropItemEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .filter { globalManager.players.getByPlayer(it.player).state != ECPlayerState.AUTHENTICATED }
                 .subscribe {
                     it.isCancelled = true
                 }

             PlayerMoveEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .filter { globalManager.players.getByPlayer(it.player).state != ECPlayerState.AUTHENTICATED }
                 .subscribe {
                     val previous = it.from
                     val to = it.to
                     val player = it.player
                     if (previous.z != to?.z && previous.x != to?.x) {
                         player.teleport(it.from)
                     }
                 }

             PlayerCommandPreprocessEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .subscribe {
                     if (globalManager.players.getByPlayer(it.player).state != ECPlayerState.AUTHENTICATED) {
                         it.isCancelled = true
                         return@subscribe
                     }

                     if (globalManager.serverConfig.adminPlayers.contains(it.player.name)) {
                         return@subscribe
                     }

                     val command = it.message.split(" ")[0].trimStart('/')
                     if (!globalManager.serverConfig.allowedCommands.contains(command)) {
                         it.isCancelled = true
                     }
                 }

            PlayerJoinEvent::class
                .observable(EventPriority.HIGHEST)
                .subscribe { event ->
                    val player = event.player
                    val ecPlayer = ECPlayer(event.player)
                    ecPlayer.playerJoinedAt = Instant.now()
                    ecPlayer.state = ECPlayerState.LOGIN
                    players[player.uniqueId] = ecPlayer

                    globalManager.runOffMainThread {
                        Logger.withTrackerPlayerEvent(player, event, "PlayerManager.PlayerJoinEvent" , "player ${player.uniqueId} error occurs when join") {
                            val discordTag = ecPlayer.database[Players.discordTag]
                            val isSent = globalManager.discord.sendVerifyMessage(event.player, discordTag, "登入账号") {
                                if (it) {
                                    ecPlayer.state = ECPlayerState.AUTHENTICATED
                                    globalManager.runInMainThread {
                                        event.player.sendMessage(globalManager.message.system("Discord 登入验证成功！"))
                                    }
                                } else {
                                    globalManager.runInMainThread {
                                        event.player.kickPlayer(globalManager.message.system("Discord 登入验证失败！"))
                                    }
                                }
                            }
                            if (!isSent) {
                                globalManager.runInMainThread {
                                    event.player.kickPlayer(globalManager.message.system("Discord $discordTag 用户不存在！"))
                                }
                            }
                        }
                    }
                }

            PlayerQuitEvent::class
                .observable(EventPriority.HIGHEST)
                .subscribe { event ->
                    val player = event.player
                    Logger.withTrackerPlayerEvent(player, event, "PlayerManager.PlayerQuitEvent" , "player ${player.uniqueId} error occurs when quit") {
                        val ecPlayer = players.remove(player.uniqueId)
                        ecPlayer!!.ensureUpdate("saving player", isAsync = true) {
                            Players.update({ Players.playerName eq player.name }) {
                                it[playerName] = player.name
                                it[uuid] = player.uniqueId.toString()
                                it[playTimes] = ecPlayer.database[playTimes] + ChronoUnit.SECONDS.between(ecPlayer.playerJoinedAt, Instant.now())
                                it[lastOnlineAt] = Instant.now().epochSecond
                            }
                        }
                    }
                }
        }
    }

    fun getByDiscordTag(tag: String): ResultRow? {
        return transaction {
            Players.select { Players.discordTag eq tag }.singleOrNull()
        }
    }

    fun getByOfflinePlayer(player: OfflinePlayer): ResultRow? {
        return transaction {
            Players.select { Players.uuid eq player.uniqueId.toString() }.singleOrNull()
        }
    }

    fun getByPlayerName(playerName: String): ResultRow? {
        return transaction {
            Players.select { Players.playerName eq playerName }.singleOrNull()
        }
    }

    fun getByPlayer(player: Player): ECPlayer {
        return players[player.uniqueId]!!
    }

}