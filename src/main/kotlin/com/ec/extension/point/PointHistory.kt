package com.ec.extension.point

import java.time.Instant

data class PointHistory(
    var point: String = "",
    var balance: Int = 0,
    var actionAt: Long = Instant.now().epochSecond,
    var type: String = "INCREMENT"
);