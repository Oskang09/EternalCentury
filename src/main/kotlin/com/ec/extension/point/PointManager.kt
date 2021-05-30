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

    fun getGradeByPoint(name: String, value: Int): Int {
        val point = points[name]!!
        return point.getGrade(value)
    }
}