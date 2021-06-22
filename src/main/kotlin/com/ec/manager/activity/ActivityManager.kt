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
        val current = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
        val event = getUpcomingActivity()
        val seconds = ChronoUnit.SECONDS.between(current, event.startInstant())
        globalManager.states.delayedTask(seconds) {
            event.onStart()
            globalManager.states.delayedTask(event.duration.toSeconds()) {
                event.onEnd()
            }

            activitySchedule()
        }
    }

    fun getUpcomingActivity(): ActivityAPI {
        val today = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
        return activities.values
            .filter { it.weekdays.contains(today.dayOfWeek) }
            .filter { it.startInstant() > today }
            .sortedBy { it.startInstant() }
            .single()
    }

    fun getTodayActivities(): List<ActivityAPI> {
        val today = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
        return activities.values
            .filter { it.weekdays.contains(today.dayOfWeek) }
            .sortedBy { it.startInstant() }
            .toList()
    }

}