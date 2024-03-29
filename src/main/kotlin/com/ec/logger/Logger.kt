package com.ec.logger

import com.ec.database.Issues
import com.ec.util.StringUtil.generateUniqueID
import com.ec.util.StringUtil.toComponent
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.SimpleDateFormat
import java.util.*

object Logger {
    private val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    private val MALAYSIA_TIMEZONE: TimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")

    fun withTrackerPlayerEvent(player: Player?, event: Event, title: String, message: String, action: LogTracker): String? {
        val trackId = withTracker(title, message, action)
        if (player != null && trackId != null) {
            if (event is Cancellable) {
                event.isCancelled = true
            }
            player.sendMessage("&b[&5系统&b] &f在运行时出现错误，请向管理员汇报 &f&l${trackId}&f。".toComponent())
        }
        return trackId
    }

    fun withTrackerPlayer(player: Player?, title: String, message: String, action: LogTracker): String? {
        val trackId = withTracker(title, message, action)
        if (player != null && trackId != null) {
            player.sendMessage("&b[&5系统&b] &f在运行时出现错误，请向管理员汇报 &f&l${trackId}&f。".toComponent())
        }
        return trackId
     }

    fun withTracker(valTitle: String, valMessage: String, action: LogTracker): String? {
        try {
            action.track()
        } catch (e: Exception) {
            val generatedId = "".generateUniqueID()
            transaction {
                Issues.insert {
                    it[id] = generatedId
                    it[title] = valTitle
                    it[message] = valMessage
                    it[timestamp] = getReadableCurrentTime()
                    it[resolved] = false
                    it[stack] = e.stackTraceToString().replace("\t", "  ").split("\r\n").toMutableList()
                }
            }
            return generatedId
        }
        return null
    }

    fun trackError(valTitle: String, valMessage: String): (Throwable) -> Unit {
        return { e ->
            val generatedId = "".generateUniqueID()
            transaction {
                Issues.insert {
                    it[id] = generatedId
                    it[title] = valTitle
                    it[message] = valMessage
                    it[timestamp] = getReadableCurrentTime()
                    it[resolved] = false
                    it[stack] = e.stackTraceToString().replace("\t", "  ").split("\r\n").toMutableList()
                }
            }
        }
    }

    fun getReadableCurrentTime(): String {
        return DATE_FORMAT.format(Calendar.getInstance(MALAYSIA_TIMEZONE).time)
    }
}