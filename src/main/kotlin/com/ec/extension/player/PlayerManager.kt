package com.ec.extension.player

import com.ec.database.Issues
import com.ec.database.Players
import com.ec.extension.GlobalManager
import com.ec.logger.Logger
import com.ec.model.player.ECPlayer
import com.ec.model.player.ECPlayerState
import dev.reactant.reactant.core.component.Component
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.lang.IllegalStateException
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

            PlayerJoinEvent::class
                .observable(EventPriority.HIGHEST)
                .subscribe { event ->
                    val player = event.player
                    Logger.withTrackerPlayerEvent(player, event, "PlayerManager.PlayerJoinEvent" , "player ${player.uniqueId} error occurs when join") {
                        val ecPlayer = ECPlayer(event.player)
                        val discordTag = ecPlayer.database[Players.discordTag]

                        ecPlayer.playerJoinedAt = Instant.now()
                        ecPlayer.state = ECPlayerState.LOGIN
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
                        players[player.uniqueId] = ecPlayer
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