package com.ec.config

import com.ec.database.enums.BattlePassType

data class BattlePassConfig(
    var type: BattlePassType,
    var level: Int,
    var experience: Int,
    var rewards: MutableList<Int>
)