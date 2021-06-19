package com.ec.extension

import com.ec.logger.Logger
import com.ec.model.Observable
import com.ec.model.ObservableMap
import com.ec.util.InputUtil
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.service.spec.server.EventService
import dev.reactant.reactant.service.spec.server.SchedulerService
import io.reactivex.rxjava3.disposables.Disposable
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

@Component
class StateManager {

    private val tickPerSecond = 20L
    private lateinit var globalManager: GlobalManager

    private val taskMapper = mutableMapOf<String, Disposable>()
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
                    teleportPlayers.remove(event.player.name)
                    teleportPlayers.values.removeIf { it == event.player.name }
                }

        }

        refreshRanking()
        continuousTask(3600) {
            refreshRanking()
        }
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

    fun delayedTask(seconds: Int, action: () -> Unit): String {
        val key = "".generateUniqueID()
        val disposer = globalManager.schedulers.timer(tickPerSecond * seconds).subscribe(action)
        taskMapper[key] = disposer
        return key
    }

    fun continuousTask(interval: Int, action: (Int) -> Unit): String {
        val key = "".generateUniqueID()
        val disposer = globalManager.schedulers.interval(tickPerSecond * interval).subscribe(action)
        taskMapper[key] = disposer
        return key
    }
}