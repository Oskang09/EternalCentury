package com.ec.config.mobs

data class MobFlagConfig(
    val visualFire: Boolean = false,
    val freezeTick: Int = 0,
    val fireTick: Int = 0,
    val noDamageTick: Int = 0,
    val shieldBlockingDelay: Int = 0,
    val removeWhenFarAway: Boolean = false,
    val glowing: Boolean = false,
)