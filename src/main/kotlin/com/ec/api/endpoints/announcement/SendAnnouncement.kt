package com.ec.api.endpoints.announcement

import com.ec.api.EndpointAPI
import com.ec.api.ResultResponse
import com.ec.config.RewardConfig
import com.ec.database.Announcements
import com.ec.database.AuditLog
import com.ec.util.ModelUtil.toJSON
import com.ec.util.StringUtil.generateUniqueID
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class SendAnnouncement: EndpointAPI() {

    override val method = HandlerType.POST
    override val path = "/announcement"

    data class Request(
        val title: String,
        val content: List<String>,
        val rewards: List<RewardConfig>,
    )

    override val handler = Handler { ctx ->
        ctx.register(AuditLog::class.java, AuditLog("Send Annoucement"))

        val request = ctx.bodyAsClass(Request::class.java)
        transaction {
            val announcement = Announcements.insert { mail ->
                mail[id] = "".generateUniqueID()
                mail[title] = request.title
                mail[content] = request.content.toMutableList()
                mail[rewards] = request.rewards.toMutableList()
                mail[createdAt] = Instant.now().epochSecond
                mail[isExpired] = false
            }
            ctx.json(ResultResponse(announcement.resultedValues!![0].toJSON()))
        }
    }

}