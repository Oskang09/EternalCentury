package com.ec.config

import com.ec.database.enums.BattlePassType

data class BattlePassConfig(
    var type: BattlePassType = BattlePassType.NORMAL,
    var level: Int = 1,
    var experience: Int = 0,
    var rewards: MutableList<Int> = mutableListOf(),
    var premiumRewards: MutableList<Int> = mutableListOf(),
)