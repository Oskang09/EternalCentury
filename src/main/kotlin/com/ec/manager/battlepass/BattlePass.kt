package com.ec.manager.battlepass

import com.ec.model.app.BattlePassReward
import com.ec.manager.GlobalManager

abstract class BattlePass(val id: String) {

    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    abstract fun levelByXp(xp: Int): Int
    abstract val exclusiveEnchantment: List<String>
    abstract val exclusiveParticleStyle: List<String>
    abstract val exclusiveParticleEffect: List<String>
    abstract val rewards: Map<Int, BattlePassReward>
    abstract val premiumRewards: Map<Int, BattlePassReward>
}