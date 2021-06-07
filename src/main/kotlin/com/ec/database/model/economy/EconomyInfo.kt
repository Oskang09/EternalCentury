package com.ec.database.model.economy

import java.time.Instant

class EconomyInfo(
    var total: Double = 0.0,
    var balance: Double = 0.0,
    var lastUpdatedAt: Long = Instant.now().epochSecond,
)