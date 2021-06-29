package com.ec.api.endpoints.players

import com.ec.api.EndpointAPI
import com.ec.api.PaginationResponse
import com.ec.database.Players
import com.ec.util.ModelUtil.toJSON
import com.ec.util.QueryUtil.iterator
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ListPlayers: EndpointAPI() {
    override val method = HandlerType.GET
    override val path = "/players"
    override val handler = Handler { ctx ->
        transaction {
            val query = Players.selectAll()
            var cursor = ""
            ctx.queryParamMap().forEach { (key, values) ->
                when (key) {
                    "id" -> query.andWhere { Players.id inList values }
                    "uuid" -> query.andWhere { Players.uuid inList values }
                    "playerName" -> query.andWhere { Players.playerName inList values }
                    "discordTag"-> query.andWhere { Players.discordTag inList values }
                    "cursor" -> cursor = values[0]
                }
            }

            val response = query.iterator(Players.id, 20, cursor)
            ctx.json(PaginationResponse(
                meta = response.cursor,
                result = response.items.toJSON(),
            ))
        }
    }
}