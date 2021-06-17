package com.ec.extension

import com.ec.logger.Logger
import com.ec.model.ObservableMap
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.service.spec.server.EventService
import dev.reactant.reactant.service.spec.server.SchedulerService
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerQuitEvent

@Component
class StateManager(
    private val schedulerService: SchedulerService,
    eventService: EventService
) {
    init {
        eventService {
            PlayerQuitEvent::class
                .observable(true, EventPriority.HIGHEST)
                .doOnError(Logger.trackError("StateManager.PlayerQuitEvent", "error occurs in event subscriber"))
                .subscribe { event ->
                    teleportPlayers.remove(event.player.name)
                    teleportPlayers.values.removeIf { it == event.player.name }
                }
        }
    }

    private val tickPerSecond = 20L
    val teleportPlayers = ObservableMap<String, String>()

    private val taskMapper = mutableMapOf<String, Disposable>()
    fun disposeTask(key: String) {
        taskMapper[key]?.dispose()
    }

    fun delayedTask(seconds: Int, action: () -> Unit): String {
        val key = "".generateUniqueID()
        val disposer = schedulerService.timer(tickPerSecond * seconds).subscribe(action)
        taskMapper[key] = disposer
        return key
    }

    fun continuousTask(interval: Int, action: (Int) -> Unit): String {
        val key = "".generateUniqueID()
        val disposer = schedulerService.interval(tickPerSecond * interval).subscribe(action)
        taskMapper[key] = disposer
        return key
    }
}