package com.ec.model.player

import com.ec.database.Players
import com.ec.database.Titles
import com.ec.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*
import javax.xml.ws.Dispatch

data class ECPlayer(var player: Player) {
    private val mutex = Mutex()

    private var uuid: UUID? = player.uniqueId
    var playerJoinedAt: Instant = Instant.now()
    var state: ECPlayerState = ECPlayerState.LOGIN
    var database: ResultRow = transaction {
        return@transaction Players.select { Players.playerName eq player.name }.single()
    }

    fun refreshPlayer() {
        transaction {
            database = Players.select { Players.playerName eq player.name }.single()
        }
    }

    fun getTitles(): List<String> {
        return transaction {
            return@transaction Titles
                .select { Titles.playerId eq database[Players.id] }
                .toMutableList()
                .map { it[Titles.titleId] }
        }
    }

    @Throws(Exception::class)
    fun ensureUpdate(action: String, isAsync: Boolean = false, update: () -> Unit): Boolean {
        var id: String? = null
        val dispatchers = if (isAsync) Dispatchers.IO else Dispatchers.Default
        runBlocking(dispatchers) {
            mutex.withLock {
                Logger.withTrackerPlayer(player, "update player - ${uuid.toString()}", action) {
                    transaction {
                        refreshPlayer()
                        update()
                        refreshPlayer()
                    }
                }
            }
        }
        return id == null
    }
}