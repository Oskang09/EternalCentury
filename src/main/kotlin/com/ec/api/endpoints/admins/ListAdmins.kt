package com.ec.api.endpoints.admins

import com.ec.api.EndpointAPI
import com.ec.api.PaginationResponse
import com.ec.database.Admins
import com.ec.util.ModelUtil.toJSON
import com.ec.util.QueryUtil.iterator
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ListAdmins : EndpointAPI() {
    override val method = HandlerType.GET
    override val path = "/admins"
    override val handler = Handler { ctx ->
        transaction {
            val query = Admins.selectAll()
            var cursor = ""
            ctx.queryParamMap().forEach { (key, values) ->
                when (key) {
                    "id" -> query.andWhere { Admins.id inList values }
                    "name" -> query.andWhere { Admins.name like values[0] }
                    "cursor" -> cursor = values[0]
                }
            }

            val response = query.iterator(Admins.id, 20, cursor)
            ctx.json(PaginationResponse(
                meta = response.cursor,
                result = response.items.toJSON(),
            ))
        }
    }
}