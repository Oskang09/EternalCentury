package com.ec.database

import org.jetbrains.exposed.sql.Table

object Transactions: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("playerId", 20)
    val transactionId = varchar("transactionId", 30)
    val orderId = varchar("orderId", 128)
    val region = varchar("region", 20)
    val method = varchar("method", 20)
    val status = varchar("status", 20)
    val currencyType = varchar("currencyType", 5)
    val amount = integer("amount")
    val transactionAt = long("transactionAt")

    override val primaryKey = PrimaryKey(id)
}
