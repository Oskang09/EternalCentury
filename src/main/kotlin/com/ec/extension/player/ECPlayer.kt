package com.ec.extension.player

import com.ec.config.PlayerData
import com.ec.extension.point.PointInfo
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.service.spec.config.Config
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.Player
import java.lang.Exception
import java.util.*

data class ECPlayer(
    var player: Player?,
    private val config: Config<PlayerData>
) {
    var uuid: UUID? = player?.uniqueId
    var isOnline: Boolean = player != null
    var data: PlayerData = config.content
    private val mutex = Mutex()

    fun ensureUpdate(
        update: (Config<PlayerData>) -> Config<PlayerData>,
        complete: ((Config<PlayerData>) -> Unit)? = null,
        fail: ((Exception) -> Unit)? = null,
    ) {
        runBlocking {
            try {
                mutex.withLock {
                    refresh()
                    update(config)
                    save()
                    if (complete != null) {
                        complete(config)
                    }
                }
            } catch (e: Exception) {
                if (fail != null) {
                    fail(e)
                }
            }
        }
    }

    private fun refresh() {
        config.refresh().blockingAwait()
        data = config.content
    }

    private fun save(fn: (() -> Unit)? = null) {
        val completable = config.save()
        completable.subscribeOn(Schedulers.io()).subscribe {
            if (fn != null) {
                fn()
            }
        }
    }
}