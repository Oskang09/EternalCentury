package com.ec.config.model

import java.time.Instant

data class EconomyHistory(
    var balance: Int = 0,
    var actionAt: Long = Instant.now().epochSecond,
    var type: EconomyType = EconomyType.DEPOSIT
)