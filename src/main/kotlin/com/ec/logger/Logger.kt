package com.ec.logger

import com.google.gson.JsonObject
import de.tr7zw.nbtapi.data.PlayerData
import dev.reactant.reactant.extra.config.type.MultiConfigs
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.apache.commons.lang.exception.ExceptionUtils
import org.bukkit.Bukkit
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import javax.net.ssl.HttpsURLConnection

object Logger {
    private val MUTEX = Mutex()
    private const val DISCORD_WEBHOOK: String = "https://discordapp.com/api/webhooks/753560505255460894/x1Px1wNlKslV1TVnEf5fV-AD60Aj8xxfMq7He2A6mSsBEsFJVejmdGF_jV3kPiKfICeL"
    private val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    private val MALAYSIA_TIMEZONE: TimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
    private val TRACK_ID_FORMAT: SimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmssSS")
    private lateinit var issues: MultiConfigs<Issue>;

    fun onInitialize(issueConfig: MultiConfigs<Issue>) {
        issues = issueConfig
    }

    private fun generateTrackId(): String {
        val id = TRACK_ID_FORMAT.format(Date())
        Thread.sleep(1)
        return id
    }

    private fun build(trackId: String, title: String, message: String, exception: Exception?): List<String> {
        val messages: MutableList<String> = mutableListOf(
            "------------- ------------- -------------",
            "Track ID : $trackId",
            "Title: $title",
            "Timestamp: " + DATE_FORMAT.format(Calendar.getInstance(MALAYSIA_TIMEZONE).time),
            "Message: $message"
        )

        if (exception != null) {
            messages.add("Exception: " + exception.message)
            messages.add(
                """
                Stack:
                ${ExceptionUtils.getStackTrace(exception).replace(",".toRegex(), "\n")}
                """.trimIndent()
            )
        }
        messages.add("------------- ------------- -------------")
        return messages
    }

    fun withTracker(title: String, message: String, action: LogTracker): String? {
        try {
            action.track()
        } catch (e: Exception) {
            val id = generateTrackId()
            issues.getOrPut("${id}.json") {
                return@getOrPut Issue(
                    id = id,
                    title = title,
                    message = message,
                    stack = e.stackTraceToString().replace("\t", "  ").split("\r\n"),
                    timestamp = DATE_FORMAT.format(Calendar.getInstance(MALAYSIA_TIMEZONE).time),
                )
            }.subscribe().dispose()
            return id
        }
        return null
    }

    fun toDiscord(id: String?, title: String, message: String, exception: Exception): String {
        var trackId = id
        if (trackId == null) {
            trackId = generateTrackId()
        }

        runBlocking {
            try {
                val url = URL(DISCORD_WEBHOOK)
                val connection = url.openConnection() as HttpsURLConnection
                connection.addRequestProperty("Content-Type", "application/json")
                connection.requestMethod = "POST"
                connection.doOutput = true
                val json = JsonObject()
                json.addProperty("content", java.lang.String.join("\n", build(trackId, title, message, exception)))
                val stream = connection.outputStream
                stream.write(json.toString().toByteArray())
                stream.close()
                connection.inputStream.close()
                connection.disconnect()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return trackId
    }

    fun toConsole(id: String?, title: String, message: String, exception: Exception): String {
        var trackId = id
        if (trackId == null) {
            trackId = generateTrackId()
        }

        runBlocking {
            launch {
                MUTEX.withLock {
                    build(trackId, title, message, exception).forEach {
                        Bukkit.getLogger().info(it)
                    }
                }
            }
        }

        return trackId
    }
}