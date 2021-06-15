package com.ec.database

import org.jetbrains.exposed.sql.Table

object Transactions: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("player_id", 20)
    val transactionId = varchar("transaction_id", 30)
    val orderId = varchar("order_id", 128)
    val region = varchar("region", 20)
    val method = varchar("method", 20)
    val status = varchar("status", 20)
    val currencyType = varchar("currency_type", 5)
    val amount = integer("amount")
    val transactionAt = long("transaction_at")

    override val primaryKey = PrimaryKey(id)
}
