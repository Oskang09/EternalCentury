package com.ec.config

data class StateConfig(
    val cooldown: MutableMap<String, Long> = mutableMapOf(),
    var inventory: MutableMap<String, List<String>> = mutableMapOf()
)