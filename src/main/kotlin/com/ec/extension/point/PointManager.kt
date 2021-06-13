package com.ec.extension.point

import com.ec.database.Players
import com.ec.database.Points
import com.ec.database.model.point.PointDetail
import com.ec.database.model.point.PointInfo
import com.ec.database.model.point.PointType
import com.ec.extension.GlobalManager
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.reactant.core.component.Component
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import kotlin.reflect.jvm.internal.impl.descriptors.PossiblyInnerType

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


    fun hasPlayerPoint(player: Player, name: String, point: Double): Boolean {
        val ecPlayer = globalManager.players.getByPlayer(player)
        val pointMapper = ecPlayer.database[Players.points]
        return (pointMapper.points[name]?.balance ?: 0.0) >= point
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
                    .andWhere { Points.type eq PointType.WITHDRAW }
                    .sumOf { it[Points.balance] },
                balance = info.balance - point,
                lastUpdatedAt = Instant.now().epochSecond
            )
            nextBalance.grade = getGradeByPoint(name, nextBalance)
            ecPlayer.database[Players.points].points[name] = nextBalance
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
            var nextBalance = PointDetail(
                total = Points
                    .select { Points.playerId eq ecPlayer.database[Players.id] }
                    .andWhere { Points.type eq PointType.DEPOSIT }
                    .sumOf { it[Points.balance] },
                balance = info.balance + point,
                lastUpdatedAt = Instant.now().epochSecond
            )
            nextBalance.grade = getGradeByPoint(name, nextBalance)
            ecPlayer.database[Players.points].points[name] = nextBalance
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