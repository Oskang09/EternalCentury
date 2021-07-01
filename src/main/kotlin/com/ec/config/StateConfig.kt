package com.ec.config

import com.ec.config.wrapper.LocationConfig

data class StateConfig(
    val cooldown: MutableMap<String, Long> = mutableMapOf(),
    var inventory: MutableMap<String, List<String>> = mutableMapOf(),
    val homes: MutableMap<String, LocationConfig> = mutableMapOf()
)