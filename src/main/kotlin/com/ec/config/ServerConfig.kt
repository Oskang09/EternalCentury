package com.ec.config

data class ServerConfig(
    val repairPrice: Float = 1F,
    val repairEnchantmentPrice: Float = 100F,
    val repairRate: Float =  1F, // 0.5 = 50%,
    var discordInfoMessage: String = ""
)
