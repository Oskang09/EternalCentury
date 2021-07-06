package com.ec.config.mobs

import org.bukkit.attribute.Attribute

data class MobConfig(
    val id: String = "",
    val name: String = "",
    val flag: MobFlagConfig = MobFlagConfig(),
    val attributes: Map<Attribute, Double> = mutableMapOf(),
    val loots: List<MobLootConfig> = listOf(),
    val skills: List<String> = listOf(),
)