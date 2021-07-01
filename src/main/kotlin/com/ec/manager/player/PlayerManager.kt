package com.ec.manager.player

import com.ec.database.Announcements
import com.ec.database.Mails
import com.ec.database.Players
import com.ec.logger.Logger
import com.ec.manager.GlobalManager
import com.ec.model.player.ECPlayer
import com.ec.model.player.ECPlayerAuthState
import com.ec.model.player.ECPlayerGameState
import com.ec.util.StringUtil.generateUniqueID
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.core.component.Component
import io.papermc.paper.event.player.AsyncChatEvent
import me.oska.config.shop.ItemConfig
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.scoreboard.Team
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.util.*

@Component
class PlayerManager {
    private val players: MutableMap<UUID, ECPlayer> = HashMap()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.events {

             PlayerDeathEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .filter { globalManager.players.getByPlayer(it.entity).gameState == ECPlayerGameState.FREE }
                 .subscribe {
                     it.keepInventory = true
                     it.keepLevel = true
                 }

             PlayerRespawnEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .filter { globalManager.players.getByPlayer(it.player).gameState == ECPlayerGameState.FREE }
                 .subscribe {
                     it.respawnLocation = globalManager.serverConfig.teleports["old-spawn"]!!.location
                 }

            AsyncChatEvent::class
                .observable(false, EventPriority.LOWEST)
                .subscribe {
                    if (!it.player.hasPermission("ec.colorchat")) {
                        it.message(it.originalMessage())
                    }
                }

             SignChangeEvent::class
                 .observable(false, EventPriority.LOWEST)
                 .subscribe {
                     if (it.player.hasPermission("ec.colorsign"))
                         it.lines().forEachIndexed { index, line ->
                             it.line(index, globalManager.message.userComponent(line))
                         }
                     }

             PlayerSwapHandItemsEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .filter { globalManager.players.getByPlayer(it.player).state == ECPlayerAuthState.AUTHENTICATED }
                 .filter { it.player.isSneaking }
                 .subscribe {
                     it.isCancelled = true
                     globalManager.inventory.displayPlayer(it.player)
                 }

             AsyncPlayerPreLoginEvent::class
                .observable(true, EventPriority.HIGHEST)
                 .doOnError(Logger.trackError("PlayerManager.AsyncPlayerPreLoginEvent", "error occurs in event subscriber"))
                .subscribe {
                    if (globalManager.serverConfig.maintenance) {
                        if (!globalManager.serverConfig.adminPlayers.contains(it.name)) {
                            it.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, globalManager.message.system("伺服器维修中。"))
                            return@subscribe
                        }
                    }
                    val player = transaction {
                        Players.select { Players.playerName eq it.name }.singleOrNull()
                    }
                    if (player == null) {
                        it.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, globalManager.message.system("请到官方Discord绑定账号后才进入伺服器。"))
                        return@subscribe
                    }
                    it.allow()
                }

             PlayerInteractEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .filter { globalManager.players.getByPlayer(it.player).state != ECPlayerAuthState.AUTHENTICATED }
                 .subscribe {
                     it.isCancelled = true
                 }

             InventoryClickEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .filter { globalManager.players.getByPlayer(it.whoClicked as Player).state != ECPlayerAuthState.AUTHENTICATED }
                 .subscribe {
                     it.isCancelled = true
                 }

             PlayerDropItemEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .filter { globalManager.players.getByPlayer(it.player).state != ECPlayerAuthState.AUTHENTICATED }
                 .subscribe {
                     it.isCancelled = true
                 }

             PlayerMoveEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .filter { globalManager.players.getByPlayer(it.player).state != ECPlayerAuthState.AUTHENTICATED }
                 .subscribe {
                     val previous = it.from
                     val to = it.to
                     val player = it.player
                     if (previous.z != to.z && previous.x != to.x) {
                         player.teleportAsync(it.from)
                     }
                 }

             PlayerCommandPreprocessEvent::class
                 .observable(true, EventPriority.HIGHEST)
                 .subscribe {
                     if (globalManager.players.getByPlayer(it.player).state != ECPlayerAuthState.AUTHENTICATED) {
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
                .doOnError(Logger.trackError("PlayerManager.PlayerJoinEvent", "error occurs in event subscriber"))
                .subscribe { event ->
                    event.joinMessage(null)
                    val player = event.player
                    val ecPlayer = ECPlayer(event.player)

                    if (!event.player.hasPlayedBefore()) {
                        event.player.teleportAsync(globalManager.serverConfig.teleports["old-spawn"]!!.location)
                        ecPlayer.ensureUpdate("saving player", isAsync = true) {
                            Players.update({ Players.playerName eq player.name }) {
                                it[playerName] = player.name
                                it[uuid] = player.uniqueId.toString()
                                it[lastOnlineAt] = Instant.now().epochSecond
                            }
                        }
                    }

                    ecPlayer.playerJoinedAt = Instant.now()
                    ecPlayer.state = ECPlayerAuthState.LOGIN
                    players[player.uniqueId] = ecPlayer

                    globalManager.permission.injectPermission(player)

                    globalManager.runOffMainThread { globalManager.titles.checkPlayerTitleAvailability(player) }

                    globalManager.runOffMainThread {
                        transaction {
                            val announcement = Announcements.select { Announcements.isExpired eq false }.toList()
                            val activeIds = announcement.map { it[Announcements.id] }
                            val sentIds = Mails.select { Mails.playerId eq ecPlayer.database[Players.id] }.map { it[Mails.announcementId] }
                            val pendingIds = activeIds.minus(sentIds)

                            announcement.filter { pendingIds.contains(it[Announcements.id]) }.forEach { result ->
                                Mails.insert {
                                    it[id] = "".generateUniqueID()
                                    it[playerId] = ecPlayer.database[Players.id]
                                    it[announcementId] = result[Announcements.id]
                                    it[title] = result[Announcements.title]
                                    it[content] = result[Announcements.content]
                                    it[rewards] = result[Announcements.rewards]
                                    it[item] = arrayListOf()
                                    it[isRead] = false
                                    it[createdAt] = result[Announcements.createdAt]
                                }
                            }

                            if (pendingIds.isNotEmpty()) {
                                player.sendMessage(globalManager.message.system("您有新的系统邮件，记得到邮箱查看哦。"))
                            }
                        }
                    }

                    globalManager.runOffMainThread {
                        val title = globalManager.titles.getPlayerActiveTitle(player)
                        if (title != null) {
                            val result = title.getDisplay()
                            globalManager.runInMainThread {
                                event.player.displayName((result + " " + player.name).toComponent())
                                event.player.playerListName((result + " " + player.name).toComponent())

                                val nameKey = player.name
                                val board = player.scoreboard
                                val team = board.getTeam(nameKey) ?: board.registerNewTeam(nameKey)
                                team.prefix("$result ".toComponent())
                                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS)
                                team.addEntry(player.name)
                                player.scoreboard = board
                            }
                        } else {
                            globalManager.runInMainThread {
                                event.player.displayName(event.player.name.toComponent())
                                event.player.displayName(event.player.name.toComponent())

                                val nameKey = player.name
                                val board = player.scoreboard
                                val team = board.getTeam(nameKey) ?: board.registerNewTeam(nameKey)
                                team.prefix("".toComponent())
                                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS)
                                team.addEntry(player.name)
                                player.scoreboard = board
                            }
                        }
                    }

                    globalManager.runOffMainThread {
                        Logger.withTrackerPlayerEvent(player, event, "PlayerManager.PlayerJoinEvent" , "player ${player.uniqueId} error occurs when join") {
                            val discordTag = ecPlayer.database[Players.discordTag]
                            val isSent = globalManager.discord.sendVerifyMessage(event.player, discordTag, "登入账号") {
                                if (it) {
                                    ecPlayer.state = ECPlayerAuthState.AUTHENTICATED
                                    globalManager.runInMainThread {
                                        event.player.sendMessage(globalManager.message.system("Discord 登入验证成功！"))
                                    }
                                } else {
                                    globalManager.runInMainThread {
                                        event.player.kick(globalManager.message.system("Discord 登入验证失败！"))
                                    }
                                }
                            }
                            if (!isSent) {
                                globalManager.runInMainThread {
                                    event.player.kick(globalManager.message.system("Discord $discordTag 用户不存在！"))
                                }
                            }
                        }
                    }
                }

            PlayerQuitEvent::class
                .observable(EventPriority.LOWEST)
                .doOnError(Logger.trackError("PlayerManager.PlayerQuitEvent", "error occurs in event subscriber"))
                .subscribe { event ->
                    event.quitMessage(null)

                    val player = event.player
                    Logger.withTrackerPlayerEvent(player, event, "PlayerManager.PlayerQuitEvent" , "player ${player.uniqueId} error occurs when quit") {
                        val ecPlayer = players.remove(player.uniqueId)

                        player.scoreboard.getTeam(player.name)?.unregister()
                        ecPlayer!!.ensureUpdate("saving player", isAsync = true) {
                            Players.update({ Players.playerName eq player.name }) {
                                it[playerName] = player.name
                                it[uuid] = player.uniqueId.toString()
                                it[lastOnlineAt] = Instant.now().epochSecond
                            }
                        }
                    }
                }
        }
    }

    fun canPlayerTeleportToTarget(player: Player, target: Player): Boolean {
        val playerWorld = player.world.name
        val targetWorld = target.world.name

        if (globalManager.serverConfig.teleportBlockedWorlds.contains(playerWorld) || globalManager.serverConfig.teleportBlockedWorlds.contains(targetWorld)) {
            return false
        }

        if (globalManager.players.getByPlayer(player).gameState == ECPlayerGameState.FREE || globalManager.players.getByPlayer(player).gameState != ECPlayerGameState.FREE) {
            return false
        }
        return true
    }

    fun refreshPlayerIfOnline(id: UUID, extraAction: ((Player) -> Unit)? = null) {
        val onlinePlayer = Bukkit.getPlayer(id)
        if (onlinePlayer != null && onlinePlayer.isOnline) {
            players[id]?.refreshPlayer()
            if (extraAction != null) {
                extraAction(onlinePlayer)
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

    fun getByPlayerId(id: String): ResultRow? {
        return transaction {
            Players.select { Players.id eq id }.singleOrNull()
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