package com.ec.manager.activity

import com.ec.manager.GlobalManager
import org.bukkit.inventory.ItemStack
import java.time.*

abstract class ActivityAPI(val id: String) {

    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract val weekdays: List<DayOfWeek>
    abstract val startHour: Int
    abstract val startMinute: Int
    abstract val duration: Duration
    abstract val display: ItemStack

    private fun current(): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
    }

    fun startInstant(): ZonedDateTime {
        return current()
            .withHour(startHour)
            .withMinute(startMinute)
            .withSecond(0)
    }

    fun endInstant(): ZonedDateTime {
        return startInstant()
            .plus(duration)
    }

    abstract fun onStart();
    abstract fun onEnd();

}