package com.ec.util

import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine
import org.bukkit.Bukkit

object HologramUtil {

    fun Hologram.onTouch(handler: TouchHandler) {
        repeat(this.size()) {
            (this.getLine(it) as TouchableLine).touchHandler = handler
        }
    }
}