package com.ec.manager.activity

import com.ec.logger.Logger
import com.ec.manager.GlobalManager
import com.ec.model.player.ECPlayerGameState
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import java.time.DayOfWeek
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

abstract class ActivityAPI(val id: String) {

    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    var running: Boolean = false

    abstract val weekdays: List<DayOfWeek>
    abstract val startHour: Int
    abstract val startMinute: Int
    abstract val duration: Duration
    abstract val display: ItemStack

    private fun current(): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
    }

    protected val disposers = mutableListOf<Disposable>()

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
        this.running = true

        globalManager.events {

            disposers.add(
                PlayerDeathEvent::class
                    .observable(false, EventPriority.LOWEST)
                    .doOnError(Logger.trackError("ActivityAPI.PlayerDeathEvent", "error occurs in event subscriber"))
                    .filter {
                        val player = globalManager.players.getByPlayer(it.entity)
                        return@filter player.gameState == ECPlayerGameState.ACTIVITY
                                && player.gameName == id
                    }
                    .subscribe { onDeath(it) }
            )

            disposers.add(
                PlayerRespawnEvent::class
                    .observable(false, EventPriority.LOWEST)
                    .doOnError(Logger.trackError("ActivityAPI.PlayerRespawnEvent", "error occurs in event subscriber"))
                    .filter {
                        val player = globalManager.players.getByPlayer(it.player)
                        return@filter player.gameState == ECPlayerGameState.ACTIVITY
                                && player.gameName == id
                    }
                    .subscribe { onRespawn(it) }
            )

            disposers.add(
                PlayerQuitEvent::class
                    .observable(false, EventPriority.LOWEST)
                    .doOnError(Logger.trackError("ActivityAPI.PlayerQuitEvent", "error occurs in event subscriber"))
                    .filter {
                        val player = globalManager.players.getByPlayer(it.player)
                        return@filter player.gameState == ECPlayerGameState.ACTIVITY
                                && player.gameName == id
                    }
                    .subscribe { onQuit(it) }
            )

        }

    }

    open fun onEnd() {
        this.running = false

        disposers.forEach { it.dispose() }
        disposers.clear()
    }

    open fun onJoinActivity(player: Player): Boolean {
        return true
    }
    open fun onQuitActivity(player: Player): Boolean {
        return true
    }

    open fun onQuit(event: PlayerQuitEvent) {}
    open fun onDeath(event: PlayerDeathEvent) {}
    open fun onRespawn(event: PlayerRespawnEvent) {}

}