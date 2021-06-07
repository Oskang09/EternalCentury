package com.ec.database.model.point

import java.time.Instant

data class PointDetail(
    var grade: Int = 0,
    var total: Double = 0.0,
    var balance: Double = 0.0,
    var lastUpdatedAt: Long = Instant.now().epochSecond,
)