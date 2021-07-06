package com.ec.config

import com.ec.model.app.Item
import com.ec.model.app.Reward

data class CrateConfig(
    val id: String = "",
    val display: ItemConfig = ItemConfig(),
    val rewards: List<CrateConfigItemReward>,
)

data class CrateConfigItemReward(
    val item: Item = Item(),
    val reward: List<Reward> = listOf()
)