package com.ec.config

data class ServerConfig(
    val repairPrice: Float = 1F,
    val repairEnchantmentPrice: Float = 100F,
    val repairRate: Float =  1F, // 0.5 = 50%,
    var discordInfoMessage: String = "",
    val allowedCommands: List<String> = listOf(),
    val adminPlayers: List<String> = listOf(),
    val signRewards: MutableMap<String, List<RewardConfig>> = mutableMapOf()
)
