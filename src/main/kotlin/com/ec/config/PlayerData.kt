package com.ec.config

import com.ec.extension.point.PointHistory
import com.ec.extension.point.PointInfo
import java.time.Instant
import java.util.*

data class PlayerData(
    var uuid: UUID = UUID(0L, 0L),
    var displayName: String = "",
    var createdAt: Long = Instant.now().epochSecond,
    var lastOnlineAt: Long = Instant.now().epochSecond,
    var currentTitle: String = "",
    var availableTitles: MutableMap<String, Long> = mutableMapOf(),
    var points: MutableMap<String, PointInfo> = mutableMapOf(),
    var pointHistory: MutableList<PointHistory> = mutableListOf(),
);