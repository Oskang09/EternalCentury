package com.ec.manager.payment

import com.ec.database.Issues
import com.ec.database.Players
import com.ec.manager.GlobalManager
import com.ec.logger.Logger
import com.ec.model.Observable
import com.ec.model.ObservableMap
import com.ec.model.ObservableMapActionType
import com.ec.util.StringUtil.generateUniqueID
import com.ec.util.StringUtil.toComponent
import com.github.revenuemonster.RevenueMonsterOpenAPI
import com.github.revenuemonster.model.Environment
import com.github.revenuemonster.model.request.OnlinePaymentRequest
import com.github.revenuemonster.model.request.OnlinePaymentRequestCustomer
import com.github.revenuemonster.model.request.OnlinePaymentRequestOrder
import com.github.revenuemonster.model.result.OnlinePaymentNotifyResponse
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@dev.reactant.reactant.core.component.Component
class PaymentManager {

    private lateinit var globalManager: GlobalManager
    private lateinit var api: RevenueMonsterOpenAPI
    private val paymentMapper  = ObservableMap<String, String>()
    private val paymentObserver: Observable<OnlinePaymentNotifyResponse> = Observable()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        paymentMapper.subscribe({ it.type == ObservableMapActionType.REMOVE }) {
            Bukkit.getPlayer(UUID.fromString(it.key))?.sendMessage(globalManager.message.system("您生成的付款网址已过期。"))
        }

        api = RevenueMonsterOpenAPI(
            Environment.valueOf(globalManager.serverConfig.payment.environment),
            globalManager.serverConfig.payment.clientId,
            globalManager.serverConfig.payment.clientSecret,
            globalManager.serverConfig.payment.privateKey
        )

        api.oauth.useAuthenticateAutoRefresh { (_, err) ->
            if (err != null) {
                transaction {
                    Issues.insert {
                        it[id] = "".generateUniqueID()
                        it[title] = "revenue monster oauth refresh"
                        it[message] = "fail to refresh"
                        it[timestamp] = Logger.getReadableCurrentTime()
                        it[resolved] = false
                        it[stack] = err.stackTraceToString().replace("\t", "  ").split("\r\n").toMutableList()
                    }
                }
            }
        }

        api.setupPayment()
    }

    fun generatePaymentURL(player: Player, amount: Int, title: String, detail: String) {
        val mapperKey = globalManager.players.getByPlayer(player).database[Players.id]
        if (paymentMapper[mapperKey] != null) {
            val builder = globalManager.message.system("&f您上次的付款请求还没过期。请到")
            builder.append(
                Component.text("&7&l[网页]")
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, paymentMapper[mapperKey]!!))
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, "点击后将前往 Revenue Monster 付款网页".toComponent()))
            )

            builder.append("&r付款，付款好点数会自动加入您的帐号。".toComponent())
            return
        }

        val checkoutApi = api.payment.createOnlinePayment(OnlinePaymentRequest(
            order = OnlinePaymentRequestOrder(
                title = title,
                detail = detail,
                additionalData = "minecraft-eternal-century:PaymentManager.getPaymentURL",
                amount = amount,
                currencyType = "MYR",
                id = "".generateUniqueID()
            ),
            customer = OnlinePaymentRequestCustomer(
                userId = mapperKey,
                email = "",
                countryCode = "",
                phoneNumber = ""
            ),
            type = "WEB_PAYMENT",
            storeId = "1597201791703943511",
            redirectUrl = "https://google.com",
            notifyUrl = "https://oskatb.ap.ngrok.io/notify"
        ))

        Logger.withTrackerPlayer(player, "PaymentManager.generatePaymentURL", "when generating payment url") {
            val result = api.getResponseFromCall(checkoutApi)
            if (result.item != null) {
                paymentMapper[mapperKey] = result.item!!.url
                paymentObserver.subscribeOnceWithTimeout(
                    { it.data.payee.userId == mapperKey },
                    1000 * 60 * 10,
                    { paymentMapper.remove(mapperKey) }
                ) {
                    transaction {
                        // TODO: Map transaction into db and process payment for player
                    }
                }

                val builder = globalManager.message.system("请到")

                builder.append(
                    Component.text("&7&l[网页]")
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, result.item!!.url))
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, "点击后将前往 Revenue Monster 付款网页".toComponent()))
                )

                builder.append("&r付款，付款好点数会自动加入您的帐号。".toComponent())
                player.sendMessage(builder)
            }
        }
    }

}