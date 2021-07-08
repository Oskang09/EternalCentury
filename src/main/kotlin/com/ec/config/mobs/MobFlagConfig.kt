package com.ec.config.mobs

data class MobFlagConfig(
    val fireTick: Int = 0,
    val noDamageTick: Int = 0,
    val shieldBlockingDelay: Int = 0,
    val removeWhenFarAway: Boolean = false,
    val glowing: Boolean = false,
)