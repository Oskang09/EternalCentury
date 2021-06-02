package com.ec.config.model

import com.ec.config.PlayerData
import com.ec.logger.Logger
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.service.spec.config.Config
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.Player
import java.lang.IllegalStateException
import java.util.*

data class ECPlayer(
    var player: Player?,
    private val config: Config<PlayerData>
) {
    var uuid: UUID? = player?.uniqueId
    var isOnline: Boolean = player != null
    var data: PlayerData = config.content
    private val mutex = Mutex()

    fun ensureUpdate(action: String, update: (Config<PlayerData>) -> Unit) {
        runBlocking(Dispatchers.IO) {
            mutex.withLock {
                refresh()
                val response = Logger.withTracker("update player - ${uuid.toString()}", action) {
                    update(config)
                }
                if (response != null) {
                    player?.sendMessage("&b[&5系统&b] &f在更新资料时出现错误，请向管理员汇报 &f&l${response}&f".colorize())
                }
                save()
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