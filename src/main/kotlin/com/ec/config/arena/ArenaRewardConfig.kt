package com.ec.config.arena

import com.ec.model.app.Reward

data class ArenaRewardConfig(
    val type: ArenaRewardConfigType,
    val reward: Reward,
)

enum class ArenaRewardConfigType {
    EQUAL,
    RANDOM
}