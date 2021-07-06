package com.ec.config.arena

import com.ec.config.wrapper.LocationConfig

data class MobArenaConfig(
    val locations: List<LocationConfig> = listOf(),
    val waves: List<MobArenaWaveConfig> = listOf(),
    val bossWave: MobArenaBossWaveConfig = MobArenaBossWaveConfig(),
)

data class MobArenaBossWaveConfig(
    val mobs: List<MobArenaWaveMobConfig> = listOf(),
    val extra: List<MobArenaWaveExtraConfig> = listOf()
)

data class MobArenaWaveConfig(
    val nextWave: Long = 0,
    val mobs: List<MobArenaWaveMobConfig> = listOf(),
    val extra: List<MobArenaWaveExtraConfig> = listOf()
)

data class MobArenaWaveMobConfig(
    val mob: String = "",
    val count: Int = 0,
)

data class MobArenaWaveExtraConfig(
    val chance: Int = 0,
    val mob: String = "",
    val count: Int = 0,
)