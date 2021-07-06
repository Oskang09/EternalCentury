package com.ec.config.arena

import com.ec.config.wrapper.LocationConfig
import com.ec.model.app.Item
import com.ec.model.app.Reward

data class ArenaConfig(
    val id: String = "",
    val info: Item = Item(),
    val spawn: List<LocationConfig> = listOf(),
    val rewards: List<Reward> = listOf(),
    val limit: Int,
    val cooldown: ArenaCooldownConfig? = null,

    val type: String = "",
    val mobArena: MobArenaConfig? = null,
)

data class ArenaCooldownConfig(
    val id: String,
    val second: Int,
)
