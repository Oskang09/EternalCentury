package com.ec.config

import com.ec.config.wrapper.LocationConfig
import com.ec.model.player.ECPlayerGameState

data class StateConfig(
    val cooldown: MutableMap<String, Long> = mutableMapOf(),
    var inventory: MutableMap<String, List<String>> = mutableMapOf(),
    val homes: MutableMap<String, LocationConfig> = mutableMapOf(),
    val battlePass: MutableMap<String, BattlePassConfig> = mutableMapOf(),
    var gameState: ECPlayerGameState = ECPlayerGameState.FREE,
    var gameName: String = ""
)