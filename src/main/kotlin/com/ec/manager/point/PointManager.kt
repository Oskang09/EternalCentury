package com.ec.manager.point

import com.ec.database.Players
import com.ec.database.Points
import com.ec.database.model.point.PointDetail
import com.ec.database.model.point.PointType
import com.ec.manager.GlobalManager
import com.ec.model.player.ECPlayer
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.reactant.core.component.Component
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

@Component
class PointManager {
    private val points: MutableMap<String, PointAPI> = mutableMapOf()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.reflections.loopPoints {
            it.initialize(globalManager)
            points[it.id] = it
        }
    }

    fun getPoints(): MutableMap<String, PointAPI> {
        return points
    }

    fun hasPlayerPoint(playerName: String, name: String, point: Double): Boolean {
        val ecPlayer = globalManager.players.getByPlayerName(playerName)!!
        val pointMapper = ecPlayer[Players.points].points
        return (pointMapper[name]?.balance ?: 0.0) >= point
    }

    fun hasPlayerPoint(player: Player, name: String, point: Double): Boolean {
        val ecPlayer = globalManager.players.getByPlayer(player)
        val pointMapper = ecPlayer.database[Players.points].points
        return (pointMapper[name]?.balance ?: 0.0) >= point
    }

    fun withdrawPlayerPoint(playerName: String, name: String, point: Double) {
        val player = globalManager.players.getByPlayerName(playerName)!!
        transaction {
            Points.insert {
                it[id] = "".generateUniqueID()
                it[playerId] = player[Players.id]
                it[type] = PointType.WITHDRAW
                it[Points.point] = name
                it[actionAt] = Instant.now().epochSecond
                it[balance] = point
            }

            val info = player[Players.points].points[name]!!
            val nextBalance = PointDetail(
                total = Points
                    .select { Points.playerId eq player[Players.id] }
                    .andWhere { Points.type eq PointType.DEPOSIT }
                    .sumOf { it[Points.balance] },
                balance = info.balance - point,
                lastUpdatedAt = Instant.now().epochSecond
            )
            nextBalance.grade = getGradeByPoint(name, nextBalance)

            val pointMaps = player[Players.points]
            pointMaps.points[name] = nextBalance
            Players.update({ Players.id eq player[Players.id]}) {
                it[points] = pointMaps
            }

            globalManager.players.refreshPlayerIfOnline(UUID.fromString(player[Players.uuid]))
        }
    }

    fun withdrawPlayerPoint(player: Player, name: String, point: Double) {
        val ecPlayer = globalManager.players.getByPlayer(player)
        ecPlayer.ensureUpdate("update points") {
            Points.insert {
                it[id] = "".generateUniqueID()
                it[playerId] = ecPlayer.database[Players.id]
                it[type] = PointType.WITHDRAW
                it[Points.point] = name
                it[actionAt] = Instant.now().epochSecond
                it[balance] = point
            }

            val info = getPointByNameFromPlayer(name, player)
            var nextBalance = PointDetail(
                total = Points
                    .select { Points.playerId eq ecPlayer.database[Players.id] }
                    .andWhere { Points.type eq PointType.DEPOSIT }
                    .sumOf { it[Points.balance] },
                balance = info.balance - point,
                lastUpdatedAt = Instant.now().epochSecond
            )
            nextBalance.grade = getGradeByPoint(name, nextBalance)

            val pointMaps = ecPlayer.database[Players.points]
            pointMaps.points[name] = nextBalance
            Players.update({ Players.id eq ecPlayer.database[Players.id]}) {
                it[points] = pointMaps
            }
        }
    }

    fun depositPlayerPoint(playerName: String, name: String, point: Double) {
        val player = globalManager.players.getByPlayerName(playerName)!!
        transaction {
            Points.insert {
                it[id] = "".generateUniqueID()
                it[playerId] = player[Players.id]
                it[type] = PointType.DEPOSIT
                it[Points.point] = name
                it[actionAt] = Instant.now().epochSecond
                it[balance] = point
            }

            val info = player[Players.points].points[name]!!
            val nextBalance = PointDetail(
                total = Points
                    .select { Points.playerId eq player[Players.id] }
                    .andWhere { Points.type eq PointType.DEPOSIT }
                    .sumOf { it[Points.balance] },
                balance = info.balance + point,
                lastUpdatedAt = Instant.now().epochSecond
            )
            nextBalance.grade = getGradeByPoint(name, nextBalance)

            val pointMaps = player[Players.points]
            pointMaps.points[name] = nextBalance
            Players.update({ Players.id eq player[Players.id]}) {
                it[points] = pointMaps
            }

            globalManager.players.refreshPlayerIfOnline(UUID.fromString(player[Players.uuid]))
        }
    }

    fun depositPlayerPoint(player: Player, name: String, point: Double) {
        val ecPlayer = globalManager.players.getByPlayer(player)
        ecPlayer.ensureUpdate("update points") {
            Points.insert {
                it[id] = "".generateUniqueID()
                it[playerId] = ecPlayer.database[Players.id]
                it[type] = PointType.DEPOSIT
                it[Points.point] = name
                it[actionAt] = Instant.now().epochSecond
                it[balance] = point
            }

            val info = getPointByNameFromPlayer(name, player)
            val nextBalance = PointDetail(
                total = Points
                    .select { Points.playerId eq ecPlayer.database[Players.id] }
                    .andWhere { Points.type eq PointType.DEPOSIT }
                    .sumOf { it[Points.balance] },
                balance = info.balance + point,
                lastUpdatedAt = Instant.now().epochSecond
            )
            nextBalance.grade = getGradeByPoint(name, nextBalance)

            val pointMaps = ecPlayer.database[Players.points]
            pointMaps.points[name] = nextBalance
            Players.update({ Players.id eq ecPlayer.database[Players.id]}) {
                it[points] = pointMaps
            }
        }
    }

    fun getPointByNameFromPlayer(name: String, player: Player): PointDetail {
        val ecPlayer = globalManager.players.getByPlayer(player)
        return ecPlayer.database[Players.points].points[name] ?: PointDetail()
    }

    fun getGradeByPoint(name: String, value: PointDetail): Int {
        val point = points[name]!!
        return point.getGrade(value)
    }
}