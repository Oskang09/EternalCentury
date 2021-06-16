package com.ec.config

data class CrateConfig(
    val requiredKey: String,
    val requiredKeyCount: Int,
    val display: ItemConfig,
    val rewards: List<RewardConfig>,
    val rewardDisplays: List<ItemConfig>,
)