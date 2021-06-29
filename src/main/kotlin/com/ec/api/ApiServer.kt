package com.ec.api

import com.ec.database.Admin
import com.ec.database.Admins
import com.ec.database.AuditLog
import com.ec.database.AuditLogs
import com.ec.database.enums.AdminStatus
import com.ec.manager.GlobalManager
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.javalin.Javalin
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

@Component
class ApiServer(val globalManager: GlobalManager): LifeCycleHook {

    private lateinit var app: Javalin

    override fun onEnable() {
        val classLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader =ApiServer::class.java.classLoader

        app = Javalin.create().start(25567)
        app.before { ctx ->
            ctx.register(AuditLog::class.java, AuditLog())

            val apiKey = ctx.header("x-api-key") ?: ""
            val admin = transaction {
                Admins.select { Admins.apiKey eq apiKey }.singleOrNull()
            } ?: throw ApiException(401, "missing x-api-key or invalid x-api-key")

            if (admin[Admins.status] == AdminStatus.INACTIVE) {
                throw ApiException(401, "admin account is deactivated")
            }

            ctx.register(Admin::class.java, Admin(admin))
        }

        app.exception(ApiException::class.java) { err, ctx ->
            ctx.status(err.status)
            ctx.json(ErrorResponse(err.error))
        }

        app.exception(Exception::class.java) { err, ctx ->
            ctx.status(500)
            ctx.json(ErrorResponse(error = "internal server error: " + err.message))
        }

        app.after { ctx ->
            val auditLog = ctx.use(AuditLog::class.java)
            if (auditLog.action != "NO_ACTION_DEFINE") {
                transaction {
                    AuditLogs.insert {
                        it[id] = "".generateUniqueID()
                        it[adminId] = ctx.use(Admin::class.java).id
                        it[action] = auditLog.action
                        it[actionAt] = Instant.now().epochSecond
                        it[log] = ActionLog(
                            ctx.fullUrl(),
                            ctx.body(),
                            ctx.resultString() ?: "",
                            ctx.status(),
                            ctx.headerMap(),
                        )
                    }
                }
            }
        }

        globalManager.reflections.loopEndpoints {
            it.globalManager = globalManager
            app.addHandler(it.method, it.path, it.handler)
        }

        Thread.currentThread().contextClassLoader = classLoader
    }

    override fun onDisable() {
        app.stop()
    }

}