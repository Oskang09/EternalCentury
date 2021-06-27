package com.ec.manager.activity

import com.ec.manager.GlobalManager
import dev.reactant.reactant.core.component.Component
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Component
class ActivityManager {

    private val activities: MutableMap<String, ActivityAPI> = mutableMapOf()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.reflections.loopActivity {
            val activity = it.getDeclaredConstructor().newInstance()
            activity.initialize(globalManager)
            activities[activity.id] = activity
        }

        activitySchedule()
    }

    private fun activitySchedule() {
        val today = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))

        // schedule tomorrow task
        val tomorrow = today.plusDays(1)
        val tomorrowSeconds = ChronoUnit.SECONDS.between(today, tomorrow)
        globalManager.states.delayedTask(tomorrowSeconds) {
            activitySchedule()
        }

        // schedule today upcoming events
        getUpcomingActivities().forEach {
            val seconds = ChronoUnit.SECONDS.between(today, it.startInstant())
            globalManager.states.delayedTask(seconds) {
                it.onStart()
                globalManager.states.delayedTask(it.duration.toSeconds()) {
                    it.onEnd()
                }
            }
        }
    }

    fun getUpcomingActivities(): List<ActivityAPI> {
        var now = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
        return activities.values
            .filter { it.weekdays.contains(now.dayOfWeek) }
            .filter { it.startInstant() >= now }
            .sortedBy { it.startInstant() }
            .toList()
    }

    fun getTodayActivities(): List<ActivityAPI> {
        val today = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
        return activities.values
            .filter { it.weekdays.contains(today.dayOfWeek) }
            .sortedBy { it.startInstant() }
            .toList()
    }

}