package com.ec.manager

import com.ec.config.StateConfig
import com.ec.database.Players
import com.ec.logger.Logger
import com.ec.model.ObservableMap
import com.ec.util.InstantUtil.toMalaysiaSystemDate
import com.ec.util.ItemUtil.toBas64
import com.ec.util.StringUtil.generateUniqueID
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.config.type.MultiConfigs
import dev.reactant.reactant.service.spec.config.Config
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Component
class StateManager(
    @Inject("plugins/EternalCentury/states")
    private val stateConfig: MultiConfigs<StateConfig>
) {

    private val tickPerSecond = 20L
    private lateinit var globalManager: GlobalManager

    private val taskMapper = mutableMapOf<String, Disposable>()
    private val states = mutableMapOf<String, Config<StateConfig>>()
    val ranks = mutableMapOf<String, Pair<Int, Double>>()
    val teleportPlayers = ObservableMap<String, String>()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.events {

            PlayerQuitEvent::class
                .observable(true, EventPriority.HIGHEST)
                .doOnError(Logger.trackError("StateManager.PlayerQuitEvent", "error occurs in event subscriber"))
                .subscribe { event ->
                    val player = event.player
                    teleportPlayers.remove(event.player.name)
                    teleportPlayers.values.removeIf { it == event.player.name }

                    val state = states.remove(player.name)!!


                    val today = Instant.now().epochSecond.toMalaysiaSystemDate()
                    state.content.inventory[today] = listOfNotNull(
                        *player.inventory.contents,
                        *player.inventory.armorContents
                    ).map { it.toBas64() }
                    while (state.content.inventory.size >= 8 ) {
                        state.content.inventory.remove(state.content.inventory.keys.minOrNull()!!)
                    }
                    state.save().subscribe()
                }

            PlayerJoinEvent::class
                .observable(true, EventPriority.HIGHEST)
                .doOnError(Logger.trackError("StateManager.PlayerJoinEvent", "error occurs in event subscriber"))
                .subscribe { event ->
                    val player = event.player
                    states[player.name] = stateConfig.getOrPut(player.uniqueId.toString() + ".json") { StateConfig() }.blockingGet()
                }

        }

        continuousTask(3600) {
            refreshRanking()
        }
    }

    private fun refreshRanking() {
        transaction {
            TransactionManager.current().exec("""
                    SELECT 
                        "player_id",
                        "type",
                        "total",
                        RANK() OVER ( 
                            PARTITION BY type
                            ORDER BY "Wallets".balance DESC
                        ) as "rank"
                    FROM 
                        Wallets
                """.trimIndent()) {
                if (it.isClosed) {
                    return@exec
                }

                val player = it.getString("player_id")
                val type = it.getString("type")
                val rank = it.getInt("rank")
                val total = it.getDouble("total")
                ranks["$player@$type"] = Pair(rank, total)
            }

            globalManager.message.broadcast("伺服排行榜刷新了,下次刷新是一小时后哦！")
        }
    }

    fun getPlayerState(player: Player): StateConfig {
        return states[player.name]!!.content
    }

    fun updatePlayerState(player: Player, updater: (StateConfig) -> Unit) {
        val config = states[player.name]!!
        updater(config.content)
        config.save().subscribe()
    }

    fun getPlayerRank(player: Player, walletName: String): Pair<Int, Double> {
        val p = globalManager.players.getByPlayerName(player.name)!!
        return ranks[p[Players.id] + "@" + walletName]!!
    }

    fun disposeTask(key: String) {
        taskMapper[key]?.dispose()
    }

    fun delayedTask(seconds: Long, action: () -> Unit): String {
        val key = "".generateUniqueID()
        val disposer = globalManager.schedulers.timer(tickPerSecond * seconds).subscribe {
            taskMapper.remove(key)
            action()
        }
        taskMapper[key] = disposer
        return key
    }

    fun continuousTask(seconds: Long, action: (Int) -> Unit): String {
        val key = "".generateUniqueID()
        val disposer = globalManager.schedulers.interval(tickPerSecond * seconds).subscribe(action)
        taskMapper[key] = disposer
        return key
    }
}