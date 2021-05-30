package com.ec.config

import com.ec.extension.point.PointHistory
import com.ec.extension.point.PointInfo
import java.util.*

data class PlayerData(
    var uuid: UUID = UUID(0L, 0L),
    val currentTitle: String = "",
    val availableTitles: MutableMap<String, Long> = mutableMapOf(),
    val points: MutableMap<String, PointInfo> = mutableMapOf(),
    val pointHistory: MutableList<PointHistory> = mutableListOf(),
);