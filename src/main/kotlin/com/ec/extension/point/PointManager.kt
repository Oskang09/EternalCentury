package com.ec.extension.point

import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import org.bukkit.entity.Player

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

    fun updatePlayerPoint(player: Player, name: String, point: Int, type: PointType) {
        val ecPlayer = globalManager.players.getByPlayer(player)!!
        ecPlayer.ensureUpdate(
            { it ->
                it.content.pointHistory.add(PointHistory(
                    point = name,
                    balance = point,
                    type = type
                ))

                val info = getPointByNameFromPlayer(name, player)
                info.total = it.content.pointHistory.filter { it.type == PointType.DEPOSIT }.sumOf { it.balance }
                info.balance += info.total - it.content.pointHistory.filter { it.type == PointType.WITHDRAW }.sumOf { it.balance }
                info.grade = getGradeByPoint(name, info.total)
                it.content.points[name] = info
                return@ensureUpdate it
            }
        )
    }

    fun getPointByNameFromPlayer(name: String, player: Player): PointInfo {
        val ecPlayer = globalManager.players.getByPlayer(player)!!
        return ecPlayer.data.points[name] ?: PointInfo(0, 0, 0)
    }

    fun getGradeByPoint(name: String, value: Int): Int {
        val point = points[name]!!
        return point.getGrade(value)
    }
}