package com.ec.config.mobs

import com.ec.config.SkillConfig
import org.bukkit.attribute.Attribute

data class MobConfig(
    val id: String = "",
    val type: String = "",
    val name: String = "",
    val xp: Int = 0,
    val flag: MobFlagConfig = MobFlagConfig(),
    val attributes: Map<Attribute, Double> = mutableMapOf(),
    val loots: List<MobLootConfig> = listOf(),
    val skills: List<SkillConfig> = listOf(),
)