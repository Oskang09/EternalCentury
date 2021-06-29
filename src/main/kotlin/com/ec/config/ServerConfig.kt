package com.ec.config

import com.ec.config.wrapper.LocationConfig

data class ServerConfig(
    val repairNpcId: Int = 0,
    val repairPrice: Float = 1F,
    val auctionNpcId: Int = 0,
    val repairEnchantmentPrice: Float = 100F,
    var repairRate: Float =  1F, // 0.5 = 50%,
    val discord: ServerDiscordConfig = ServerDiscordConfig(),
    val payment: ServerPaymentConfig = ServerPaymentConfig(),
    var maintenance: Boolean = false,
    val allowedCommands: List<String> = listOf(),
    val adminPlayers: List<String> = listOf(),
    val teleportBlockedWorlds: List<String> = listOf(),
    val teleports: MutableMap<String, LocationConfig> = mutableMapOf(),
    val signRewards: MutableMap<String, List<RewardConfig>> = mutableMapOf()
)

data class ServerPaymentConfig(
    val environment: String = "",
    val clientId: String ="",
    val clientSecret: String = "",
    val privateKey: String = "",
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
    val ruleChannel: String = "",
    val chatAnnouncement: String = ""
)
