package com.ec.api.endpoints

import com.ec.api.EndpointAPI
import com.ec.api.ErrorResponse
import com.ec.api.ResultResponse
import com.ec.config.RewardConfig
import com.ec.database.Announcements
import com.ec.database.Mails
import com.ec.database.Players
import com.ec.util.StringUtil.generateUniqueID
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class SendAnnouncement: EndpointAPI() {

    override val method: HandlerType = HandlerType.POST
    override val path: String = "/announcement"

    data class Request(
        val title: String,
        val content: String,
        val rewards: MutableList<RewardConfig>,
    )

    override val handler: Handler = Handler { ctx ->
        val request = ctx.bodyAsClass(Request::class.java)
        transaction {
            Announcements.insert { mail ->
                mail[id] = "".generateUniqueID()
                mail[title] = request.title
                mail[content] = request.content
                mail[rewards] = request.rewards
                mail[createdAt] = Instant.now().epochSecond
                mail[isExpired] = false
            }
        }

        ctx.json(ResultResponse(result = "announcement sent successfully"))
    }

}