package com.ec.manager

import com.ec.config.StateConfig
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
    val balanceRanks = mutableMapOf<String, Int>()
    val mcmmoRanks = mutableMapOf<String, Int>()
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

        refreshRanking()
        continuousTask(3600) {
            refreshRanking()
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

    private fun refreshRanking() {
        transaction {
            TransactionManager.current().exec("""
                    SELECT 
                        id, 
                        RANK() OVER ( ORDER BY json_extract("Players".balance, '$.balance') DESC ) as rank
                    FROM Players 
                """.trimIndent()) {
                balanceRanks[it.getString("id")] = it.getInt("rank")
            }

            TransactionManager.current().exec("""
                    SELECT 
                        player_id, 
                        RANK() OVER ( ORDER BY "Points".balance DESC ) as rank
                    FROM Points
                """.trimIndent()) {
                mcmmoRanks[it.getString("player_id")] = it.getInt("rank")
            }

            globalManager.message.broadcast("伺服排行榜已经刷新了。")
        }
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