package com.ec.config

import com.ec.config.model.EconomyHistory
import com.ec.config.model.EconomyInfo
import com.ec.config.model.PointHistory
import com.ec.config.model.PointInfo
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
    var balance: EconomyInfo = EconomyInfo(0, 0),
    var balanceHistory: MutableList<EconomyHistory> = mutableListOf(),
    var permissions: MutableList<String> = mutableListOf(),
);