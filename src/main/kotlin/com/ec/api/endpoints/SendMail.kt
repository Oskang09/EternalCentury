package com.ec.api.endpoints

import com.ec.api.EndpointAPI
import com.ec.api.ApiException
import com.ec.api.ResultResponse
import com.ec.config.RewardConfig
import com.ec.database.AuditLog
import com.ec.database.Mails
import com.ec.database.Players
import com.ec.util.ModelUtil.toJSON
import com.ec.util.StringUtil.generateUniqueID
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class SendMail: EndpointAPI() {

    override val method = HandlerType.POST
    override val path = "/mail"

    data class Request(
        val player: String,
        val title: String,
        val content: List<String>,
        val rewards: List<RewardConfig> ,
    )

    override val handler = Handler { ctx ->
        ctx.register(AuditLog::class.java, AuditLog("Send Mail"))

        val request = ctx.bodyAsClass(Request::class.java)
        val targetPlayer = transaction {  Players.select{ Players.playerName eq request.player }.singleOrNull() }
        when (targetPlayer == null) {
            true -> throw ApiException(404, "player doesn't exists")
            else -> {
                transaction {
                    val res = Mails.insert { mail ->
                        mail[id] = "".generateUniqueID()
                        mail[playerId] = targetPlayer[Players.id]
                        mail[title] = request.title
                        mail[content] = request.content.toMutableList()
                        mail[rewards] = request.rewards.toMutableList()
                        mail[item] = arrayListOf()
                        mail[isRead] = false
                        mail[createdAt] = Instant.now().epochSecond
                    }

                    ctx.json(ResultResponse(res.resultedValues!![0].toJSON()))
                }
            }
        }
    }

}