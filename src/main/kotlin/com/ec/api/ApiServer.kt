package com.ec.api

import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.javalin.Javalin

@Component
class ApiServer(val globalManager: GlobalManager): LifeCycleHook {

    private lateinit var app: Javalin

    override fun onEnable() {
        val classLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader =ApiServer::class.java.classLoader

        app = Javalin.create().start(25567)
        app.before { ctx ->
            val apiKey = ctx.header("x-api-key")
            if (apiKey != globalManager.serverConfig.apiKey) {
                ctx.status(401).json(ErrorResponse(
                    message = "missing com.ec.api key or invalid com.ec.api key"
                ))
                return@before
            }
        }

        globalManager.reflections.loopEndpoints {
            app.addHandler(it.method, it.path, it.handler)
        }

        Thread.currentThread().contextClassLoader = classLoader
    }

    override fun onDisable() {
        app.stop()
    }

}