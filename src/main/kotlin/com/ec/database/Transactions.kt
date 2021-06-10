package com.ec.database

import com.ec.database.model.TransactionStatus
import com.ec.database.types.enum
import org.jetbrains.exposed.sql.Table

object Transactions: Table() {
    val id = varchar("id", 20)
    val transactionCode = varchar("transaction_qrcode", 30)
    val transactionId = varchar("transaction_id", 30)
    val orderId = varchar("order_id", 128)
    val method = varchar("method", 20)
    val status = enum<TransactionStatus>("status")
    val amount = integer("amount")
    val actionAt = long("action_at")

    override val primaryKey = PrimaryKey(id)
}
