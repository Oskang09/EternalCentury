package com.ec.api.endpoints

import com.ec.api.EndpointAPI
import com.ec.api.PaginationResponse
import com.ec.database.Transactions
import com.ec.util.ModelUtil.toJSON
import com.ec.util.QueryUtil.iterator
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ListTransactions: EndpointAPI() {
    override val method = HandlerType.GET
    override val path = "/transactions"
    override val handler = Handler { ctx ->
        transaction {
            val query = Transactions.selectAll()
            var cursor = ""
            ctx.queryParamMap().forEach { (key, values) ->
                when (key) {
                    "id" -> query.andWhere { Transactions.id inList values }
                    "playerId" -> query.andWhere { Transactions.playerId inList values }
                    "transactionId" -> query.andWhere { Transactions.transactionId inList values }
                    "orderId" -> query.andWhere { Transactions.orderId inList values }
                    "method" -> query.andWhere { Transactions.method inList values }
                    "cursor" -> cursor = values[0]
                }
            }

            val response = query.iterator(Transactions.id, 20, cursor)
            ctx.json(PaginationResponse(
                meta = response.cursor,
                result = response.items.toJSON(),
            ))
        }
    }
}