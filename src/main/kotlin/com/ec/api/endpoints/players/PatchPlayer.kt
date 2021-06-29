package com.ec.api.endpoints.players

import com.ec.api.EndpointAPI
import com.ec.api.ApiException
import com.ec.api.ResultResponse
import com.ec.database.AuditLog
import com.ec.database.Players
import com.ec.database.enums.PlayerStatus
import com.ec.util.ModelUtil.toJSON
import io.javalin.http.Handler
import io.javalin.http.HandlerType
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

class PatchPlayer: EndpointAPI() {
    override val method = HandlerType.PATCH
    override val path = "/player/:id"

    data class Request(
        val status: PlayerStatus,
        val reason: String
    )

    override val handler = Handler { ctx ->
        ctx.register(AuditLog::class.java, AuditLog("Patch Player"))

        val request = ctx.bodyAsClass(Request::class.java)
        val id = ctx.pathParam("id")
        val updateCount = transaction {
            Players.update({ Players.id eq id }) { p ->
                p[status] = request.status
                p[reason] = request.reason
            }
        }

        when (updateCount) {
            1 -> {
                val player = globalManager.players.getByPlayerId(id)!!
                globalManager.players.refreshPlayerIfOnline(UUID.fromString(player[Players.uuid])) {
                    when (player[Players.status]) {
                        PlayerStatus.SUSPEND, PlayerStatus.BAN ->
                            it.kick(globalManager.message.system(
                                "&f您的账号被停止或者黑名单\n&f[&5原因&f] &7- &e" + player[Players.reason]
                            ))
                        else -> {}
                    }
                }
                ctx.json(ResultResponse(player.toJSON()))
            }
            else -> throw ApiException(404, "fail to patch player")
        }

    }
}