package com.ec.extension.payment

import com.ec.database.Issues
import com.ec.extension.GlobalManager
import com.ec.logger.Logger
import com.ec.util.StringUtil.colorize
import com.ec.util.StringUtil.generateUniqueID
import com.github.revenuemonster.RevenueMonsterOpenAPI
import com.github.revenuemonster.model.Environment
import com.github.revenuemonster.model.request.GetQRCodeByCheckoutIDRequest
import com.github.revenuemonster.model.request.OnlinePaymentRequest
import com.github.revenuemonster.model.request.OnlinePaymentRequestCustomer
import com.github.revenuemonster.model.request.OnlinePaymentRequestOrder
import de.themoep.minedown.MineDown
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.extensions.itemMeta
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.AlphaComposite
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.nio.Buffer
import java.time.Instant
import java.util.*
import javax.imageio.ImageIO

@Component
class PaymentManager {

    private val players: MutableList<UUID> = mutableListOf()
    private lateinit var globalManager: GlobalManager
    private lateinit var api: RevenueMonsterOpenAPI

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

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
                        it[stack] = err.stackTraceToString().replace("\t", "  ").split("\r\n").toMutableList()
                    }
                }
            }
        }

        api.setupPayment()
    }

    fun generatePaymentURL(player: Player, amount: Int, title: String, detail: String) {
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
                userId = player.uniqueId.toString(),
                email = "",
                countryCode = "",
                phoneNumber = ""
            ),
            type = "WEB_PAYMENT",
            storeId = "1597201791703943511",
            redirectUrl = "https://google.com",
            notifyUrl = "https://oskatb.ap.ngrok.io/notify"
        ))

        Logger.withTrackerPlayer(player, "get payment url", "when generating payment url") {
            val result = api.getResponseFromCall(checkoutApi)
            if (result.item != null) {
                val builder = ComponentBuilder()
                builder.append(globalManager.message.system("请到"))

                val message = TextComponent("&7&l[网页]".colorize())
                message.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, result.item!!.url)
                message.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("点击后将前往 Revenue Monster 付款网页"))
                builder.append(message)

                builder.append("&r付款，付款好点数会自动加入您的帐号。".colorize())

                player.spigot().sendMessage(ChatMessageType.CHAT, *builder.create())
            }
        }
    }

}