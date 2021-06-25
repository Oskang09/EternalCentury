package com.ec.config

data class ServerConfig(
    val repairNpcId: Int = 0,
    val repairPrice: Float = 1F,
    val auctionNpcId: Int = 0,
    val repairEnchantmentPrice: Float = 100F,
    var repairRate: Float =  1F, // 0.5 = 50%,
    val apiKey: String = "",
    val discord: ServerDiscordConfig = ServerDiscordConfig(),
    var maintenance: Boolean = false,
    val allowedCommands: List<String> = listOf(),
    val adminPlayers: List<String> = listOf(),
    val teleportBlockedWorlds: List<String> = listOf(),
    val teleports: MutableMap<String, LocationConfig> = mutableMapOf(),
    val signRewards: MutableMap<String, List<RewardConfig>> = mutableMapOf()
)

data class ServerDiscordConfig(
    val token: String = "",
    val guild: String = "",
    var infoMessage: String = "",
    val infoChannel: String = "",
    val ruleMessage: String = "",
    val registerChannel: String = "",
    val newbieRole: String = "",
    val playerRole: String = "",
    val chatAnnouncement: String = ""
)
