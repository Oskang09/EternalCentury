package com.ec.database

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Wallets: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("playerId", 20)
    val type = varchar("type", 20)
    val grade = integer("grade")
    val total = double("total")
    val balance = double("balance")
    val updatedAt = long("updatedAt")

    override val primaryKey = PrimaryKey(id)
}

data class Wallet(
    val id: String = "",
    var type: String = "",
    var grade: Int = 1,
    var total: Double = 0.0,
    var balance: Double = 0.0,
) {

    constructor(row: ResultRow): this(
        row[Wallets.id],
        row[Wallets.type],
        row[Wallets.grade],
        row[Wallets.total],
        row[Wallets.balance],
    )
}