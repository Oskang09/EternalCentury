package com.ec.extension.player

import com.ec.config.PlayerData
import com.ec.extension.point.PointHistory
import com.ec.extension.point.PointInfo
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.service.spec.config.Config
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.Player
import java.lang.Exception
import java.util.*
import kotlin.math.hypot

@Component
data class ECPlayer(
    var player: Player?,
    private val config: Config<PlayerData>
) {
    var uuid: UUID? = player?.uniqueId
    var isOnline: Boolean = player != null
    var data: PlayerData = config.content
    private val mutex = Mutex()

    fun getAvailableTitles(): MutableMap<String, Long> {
        return config.content.availableTitles
    }

    fun getCurrentTitle(): String {
        return config.content.currentTitle
    }

    fun getPointByName(point: String): PointInfo {
        return config.content.points[point] ?:
        return PointInfo(0,0,0)
    }

//    fun removePointByName(name: String, point: Int, grade: Int) {
//        runBlocking {
//            MUTEX.withLock {
//                refresh()
//
//                config.content.pointHistory.add(PointHistory(
//                    point = name,
//                    balance = point,
//                    type = "WITHDRAW"
//                ))
//
//                val info = getPointByName(name)
//                info.grade = grade
//                info.balance += point
//                info.total = config.content.pointHistory.sumOf { it.balance }
//                config.content.points[name] = info
//
//                save()
//            }
//        }
//    }
//
//    fun addPointByName(name: String, point: Int, grade: Int) {
//        runBlocking {
//            MUTEX.withLock {
//                refresh()
//
//                config.content.pointHistory.add(PointHistory(
//                    point = name,
//                    balance = point,
//                    type = "DEPOSIT"
//                ))
//
//                val info = getPointByName(name)
//                info.grade = grade
//                info.balance += point
//                info.total = config.content.pointHistory.sumOf { it.balance }
//                config.content.points[name] = info
//
//                save()
//            }
//        }
//    }

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