package com.ec.config.arena

import com.ec.config.wrapper.LocationConfig
import com.ec.model.app.Item
import com.ec.model.app.Reward

data class ArenaConfig(
    val id: String = "",
    val name: String = "",
    val info: Item = Item(),
    val limit: Int,
    val spawn: List<LocationConfig> = listOf(),
    val rewards: List<ArenaRewardConfig> = listOf(),
    val cooldown: ArenaCooldownConfig? = null,

    val arenaType: ArenaType = ArenaType.MOBARENA,
    val mobArena: MobArenaConfig? = null,
)

enum class ArenaType {
    MOBARENA
}

data class ArenaCooldownConfig(
    val id: String,
    val second: Int,
)
