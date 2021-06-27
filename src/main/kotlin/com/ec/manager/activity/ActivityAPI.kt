package com.ec.manager.activity

import com.ec.manager.GlobalManager
import com.ec.model.player.ECPlayerGameState
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import java.time.*

abstract class ActivityAPI(val id: String) {

    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract val weekdays: List<DayOfWeek>
    abstract val startHour: Int
    abstract val startMinute: Int
    abstract val duration: Duration
    abstract val display: ItemStack

    private fun current(): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
    }

    private val disposers = mutableListOf<Disposable>()

    fun startInstant(): ZonedDateTime {
        return current()
            .withHour(startHour)
            .withMinute(startMinute)
            .withSecond(0)
    }

    fun endInstant(): ZonedDateTime {
        return startInstant()
            .plus(duration)
    }

    open fun onStart() {

        globalManager.events {

            disposers.add(
                PlayerDeathEvent::class
                    .observable(false, EventPriority.HIGHEST)
                    .filter {
                        val player = globalManager.players.getByPlayer(it.entity)
                        return@filter player.gameState == ECPlayerGameState.ACTIVITY
                                && player.activityType == id
                    }
                    .subscribe { onDeath(it) }
            )

            disposers.add(
                PlayerRespawnEvent::class
                    .observable(false, EventPriority.HIGHEST)
                    .filter {
                        val player = globalManager.players.getByPlayer(it.player)
                        return@filter player.gameState == ECPlayerGameState.ACTIVITY
                                && player.activityType == id
                    }
                    .subscribe { onRespawn(it) }
            )

            disposers.add(
                PlayerQuitEvent::class
                    .observable(false, EventPriority.HIGHEST)
                    .filter {
                        val player = globalManager.players.getByPlayer(it.player)
                        return@filter player.gameState == ECPlayerGameState.ACTIVITY
                                && player.activityType == id
                    }
                    .subscribe { onQuit(it) }
            )

        }

    }

    open fun onEnd() {
        disposers.forEach { it.dispose() }
    }

    abstract fun onQuit(event: PlayerQuitEvent)
    abstract fun onDeath(event: PlayerDeathEvent)
    abstract fun onRespawn(event: PlayerRespawnEvent)

}