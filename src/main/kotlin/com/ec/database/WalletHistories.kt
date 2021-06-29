package com.ec.database

import com.ec.database.enums.WalletAction
import com.ec.database.types.enum
import org.jetbrains.exposed.sql.Table

object WalletHistories: Table() {
    val id = varchar("id", 20)
    val playerId = varchar("playerId", 20)
    val action = enumerationByName("action", 20, WalletAction::class)
    val type = varchar("type", 20)
    val balance = double("balance")
    val actionAt = long("actionAt")

    override val primaryKey = PrimaryKey(id)
}