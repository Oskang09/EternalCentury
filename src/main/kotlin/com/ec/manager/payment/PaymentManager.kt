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

    private val players: MutableList<UUID> = mutableListOf()
    private lateinit var globalManager: GlobalManager
    private lateinit var api: RevenueMonsterOpenAPI
    private val paymentMapper  = ObservableMap<String, String>()
    val paymentObserver: Observable<OnlinePaymentNotifyResponse> = Observable()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        paymentMapper.subscribe({ it.type == ObservableMapActionType.REMOVE }) {
            Bukkit.getPlayer(UUID.fromString(it.key))?.sendMessage(globalManager.message.system("您生成的付款网址已过期。"))
        }

        api = RevenueMonsterOpenAPI(
            Environment.SANDBOX, "1616686697740585784", "uZsYdmzTsJzCQbAjcPMKGfFrYVclGxQX",
"-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEoQIBAAKCAQB0ANraopd5Kcc9VQl6zHyrzL2g5RH1lchWoD0y6yWHXG7VJeF6\n" +
            "C6FR9eRvV4NIA8LH7ttalA7H8jqPz8FCJo2/I2CbAejjOcZVhniG3JbsX9/rfnNm\n" +
            "OsUPHazFiYeK1bMYDNuVtlqd/SBIwVyRi/ylp1B4A0Sn7e0AY/t3TP5l9C/klZ1o\n" +
            "Eu9tpX6o24NGPjPdPD7/pp38HO3AngSXew71SagcdbXxv6cbYTXAF0UcCnBwcVO+\n" +
            "PAu6+DSj0r9xMo3xlBUphNUCj+qSBJgGgi1AdM1y/Qo01oi1+MN39Ea9adNN+QFt\n" +
            "BFYQCRZbJl/OlcXxISlcDGeq4+U55W7V/sGlAgMBAAECggEAAgSRygPSBrWHVbXI\n" +
            "+G3eLU7ebZIOgesdFQSsi9ozSOt+sg56oZjaMYbJdnZbPkFyfe/VuPmiWDAKfL3s\n" +
            "aq4pAQ4ofAnId0tl+87fAdmMdogkaQBGGZ0kGGM3wifmR6/38Y8nsq79XIouqZVT\n" +
            "euSofGkwqSXFZ/ZnjP4wPZ8FPdi/i67hNEtlnKdJRsM3Rp3O5zzIzf2ZrzdD0LlT\n" +
            "D+G90ZUSmFScrQZwhareKCLjvGHbqR7EMgJDejW1rJbjpsNodVJXdsDKUuNtWRQ9\n" +
            "wP5XsiBvatD8nAtLxpatSK33Z/DLhjL7XzSpi8bOQZuIj6UVHvINiLTfTAKa7YCX\n" +
            "+3CKAQKBgQC1dmJpVYAkAgcwSbOykQptU+lEemxl2NqFjW8YODz41HscMKDCVPWS\n" +
            "lLF5v2Nkx4cjHVCPkD3MZ02VosXKAS+fFId5CizOVezS7cM3Gqqktf/GF41ofiN3\n" +
            "r9gTYARJKwx+yAIgyXHVpP7gzJOFalEnkpucrplbCyWFCOCKcwqZIQKBgQCjpySh\n" +
            "2A1tF+E9vkJODPykKxQXihOq7gy6105o9RGwxzsxyBSARPW/KzPQ4tNX9dbUSn+v\n" +
            "WI6H+nk+YCNcXVwIv604K9y5f1TkU/RkPKmRav03IduM0pD5PxCMTuYXMU8UE1ML\n" +
            "Q3PK37ua90Q3/mWuiAbKCW2ouHenIPVJ4X1EBQKBgA2M/6BaEC2gMSU7+71T83Fi\n" +
            "mMLSWZHpdbgPbcJjQLpcM61RPFAGxCfkDrTGxAdclwzaPY/a96Jx/Gs2Mor5N7Mr\n" +
            "d0pkph/qbrr5omBVD3UpWiZSz+6DrOZdLUeVHfzQyCgXi4EjSeroXVgwLrwBynmo\n" +
            "CxLSPwV7eZvLo+jy2lHBAoGAaSIZYHehuHHc24N8qROiwfyCvdSQagDf4LAsyTSX\n" +
            "FtAG8SYuNXEXxqYEda8iQqHGTz9E4+qqNiTs+utcDBxV4bDxoOJcvDZW3RAqMrLd\n" +
            "5HOtFFwF5WPoipa/FMQjAMdGnAkGEnhUzQIKTbWH98jQndz5L5X7AqbvB0kfC0V2\n" +
            "6dkCgYBeBg/VBhF6kjHrewz4E7vDkQmcSc765NZlCjGLP2RzfUJh2Ncfjv2hNWnT\n" +
            "6UManxCW+Jz7ngsQMYxU6nIP3q5PuGeE+px+VAOQ9HTX0EvJy4oDBLdk63UWpA/M\n" +
            "ILK1qftMsnMslSu0EpjxvqwifiOoNfOSIcC1+P8k+yF3mX6gCg==\n" +
            "-----END RSA PRIVATE KEY-----",
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