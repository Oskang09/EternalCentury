package com.ec.extension.discord

import club.minnced.jda.reactor.ReactiveEventManager
import club.minnced.jda.reactor.on
import club.minnced.jda.reactor.onMessage
import com.ec.database.Players
import com.ec.database.model.ChatType
import com.ec.database.model.economy.EconomyInfo
import com.ec.database.model.point.PointInfo
import com.ec.extension.GlobalManager
import com.ec.model.ObservableObject
import com.ec.util.RandomUtil
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.time.Instant
import java.util.EnumSet
import kotlin.time.ExperimentalTime

@Component
class DiscordManager: LifeCycleHook {
    private lateinit var globalManager: GlobalManager
    private lateinit var jda: JDA
    private lateinit var guild: Guild
    private lateinit var newbieRole: Role
    private lateinit var playerRole: Role
    private lateinit var verifyObservableObject: ObservableObject<MessageReactionAddEvent>
    private var channels: MutableMap<ChatType, TextChannel> = mutableMapOf()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        val nameRegex = Regex("^\\w{3,16}\$", options = setOf(RegexOption.IGNORE_CASE))
        val manager = ReactiveEventManager()

        jda = JDABuilder.createDefault("ODUwODA2NzM0MTAzNTExMDUx.YLvFhA.LYqkwvn3AhzotVnBWP5S5ZqTrto")
            .setEnabledIntents(EnumSet.allOf(GatewayIntent::class.java))
            .setEventManager(manager)
            .build()
            .awaitReady()

        jda.presence.setPresence(OnlineStatus.ONLINE, Activity.playing("Minecraft"), false)

        guild = jda.getGuildById(851153973432156191)!!
        newbieRole = guild.getRoleById(851390242740895754)!!
        playerRole = guild.getRoleById(851158488075862077)!!

        mutableMapOf(
            ChatType.GLOBAL to "851155982558167050",
            ChatType.MCMMO to "851156370334285864",
            ChatType.SURVIVAL to "851154328286658590",
            ChatType.PVPVE to "851155341534429204"
        ).forEach { (chatType, channelId) ->
            channels[chatType] = guild.getTextChannelById(channelId) !!
        }

        globalManager.events {

            AsyncPlayerChatEvent::class
                .observable(false, EventPriority.LOWEST)
                .subscribe {
                    it.isCancelled = true

                    val sentMessage = it.message
                    channels[ChatType.GLOBAL]?.sendMessage(sentMessage)
                }

        }

        val embed = EmbedBuilder();
        embed.setColor(Color.GREEN)
        embed.setThumbnail("https://ngrok.oskadev.com/file/mc-logo")
        embed.setAuthor("永恒新世纪 Eternal Century ", "https://minecraft.oskadev.com", "https://ngrok.oskadev.com/file/mc-logo")
        embed.setTitle("伺服器状态 - 在线 ( Online )")
        embed.setDescription("新来的玩家可以到 \'伺服规则\' 频道，同意接受规则后才能进行注册。")
        embed.addField("地区配置", "", true)
        embed.addField("人数限制", "50", true)
        embed.addField("目前版本", "0.0.1", true)
        embed.addField("伺服DC", "https://discord.gg/7rfSfwQBct", true)
        embed.addField("伺服IP", "survival.oskadev.com", true)
        embed.setFooter("伺服器咨询")
        embed.setTimestamp(Instant.now())

        if (globalManager.serverConfig.discordInfoMessage == "") {
            globalManager.serverConfig.discordInfoMessage = guild.getTextChannelById(851153973960114178)!!.sendMessage(embed.build()).complete().id
        } else {
            val serverStatusMessage = guild.getTextChannelById(851153973960114178)!!.retrieveMessageById(globalManager.serverConfig.discordInfoMessage).complete()
            globalManager.serverConfig.discordInfoMessage = serverStatusMessage.editMessage(embed.build()).complete().id
        }

        verifyObservableObject = ObservableObject(jda.on())
        verifyObservableObject.subscribe({ it.member?.roles?.contains(newbieRole) == true && it.messageId == "851396531298238477" }) {
            guild.addRoleToMember(it.member!!.idLong, newbieRole).complete()
        }

        jda.getTextChannelById("851158046020599868")!!
            .onMessage()
            .doOnNext { it.message.delete().complete() }
            .filter { globalManager.players.getByDiscordTag(it.message.author.asTag) == null }
            .subscribe {
                val name = it.message.contentRaw
                if (!nameRegex.containsMatchIn(name)) {
                    it.channel.sendMessage("<@${it.message.author.id}> $name 不是一个合法的名字。").complete()
                    return@subscribe
                }

                if (globalManager.players.getByPlayerName(name) != null) {
                    it.channel.sendMessage("<@${it.message.author.id}> $name 已经被使用了。").complete()
                    return@subscribe
                }

                transaction {
                    Players.insert { data ->
                        data[id] = "".generateUniqueID()
                        data[uuid] = null
                        data[playerName] = name
                        data[discordTag] = it.message.author.asTag
                        data[playTimes] = 0
                        data[createdAt] = Instant.now().epochSecond
                        data[currentTitle] = ""
                        data[balance] = EconomyInfo()
                        data[points] = PointInfo()
                        data[lastOnlineAt] = Instant.now().epochSecond
                        data[enchantmentRandomSeed] = RandomUtil.randomInteger(99999)
                        data[channels] = ChatType.values().toMutableList()
                        data[blockedTeleport] = mutableListOf()
                    }
                }

                it.channel.sendMessage("<@${it.message.author.id}> $name 账号注册成功！")
                guild.modifyNickname(it.member!!, name)
                guild.removeRoleFromMember(it.message.author.idLong, newbieRole).complete()
                guild.addRoleToMember(it.message.author.idLong, playerRole).complete()
            }
    }

    override fun onDisable() {
        val embed = EmbedBuilder();
        embed.setColor(Color.RED)
        embed.setThumbnail("https://ngrok.oskadev.com/file/mc-logo")
        embed.setAuthor("永恒新世纪 Eternal Century ", "https://minecraft.oskadev.com", "https://ngrok.oskadev.com/file/mc-logo")
        embed.setTitle("伺服器状态 - 离线 ( Offline )")
        embed.setDescription("伺服器离线中，请等待伺服器上线后再申请账号。")
        embed.addField("地区配置", "", true)
        embed.addField("人数限制", "50", true)
        embed.addField("目前版本", "0.0.1", true)
        embed.addField("伺服DC", "https://discord.gg/7rfSfwQBct", true)
        embed.addField("伺服IP", "survival.oskadev.com", true)
        embed.setFooter("伺服器咨询")
        embed.setTimestamp(Instant.now())

        verifyObservableObject.dispose()

        val serverStatusMessage = guild.getTextChannelById(851153973960114178)!!.retrieveMessageById(globalManager.serverConfig.discordInfoMessage).complete()
        globalManager.serverConfig.discordInfoMessage = serverStatusMessage.editMessage(embed.build()).complete().id

        jda.shutdown()
    }

    @OptIn(ExperimentalTime::class)
    fun sendVerifyMessage(player: Player, userTag: String, verifyType: String, callback: (Boolean) -> Unit): Boolean {
        val members = guild.findMembers { it.user.asTag == userTag }.get()
        if (members.size != 1) {
            return false
        }

        val embed = EmbedBuilder();
        embed.setThumbnail("https://minotar.net/helm/${player.name}/100.png")
        embed.setAuthor("永恒新世纪 Eternal Century ", "https://minecraft.oskadev.com", "https://minotar.net/helm/${player.name}/100.png")
        embed.setTitle("您的账号在伺服器发出验证请求，您必须通过请求才能继续！")
        embed.setDescription("您可以通过此讯息下面的表情图案来进行认证。")
        embed.setFooter("请求类型 ： $verifyType")
        embed.setTimestamp(Instant.now())

        val user = members[0].user
        val channel = user.openPrivateChannel().complete()
        val message = channel.sendMessage(embed.build()).complete()

        verifyObservableObject.subscribeOnceWithTimeout(
            { event -> !event.user!!.isBot && event.messageId == message.id },
            timeout = 30000L, onTimeout = { callback(false) }
        ) {
            callback(it.reactionEmote.asCodepoints == "U+2705")
        }

        message.addReaction("✅").complete()
        message.addReaction("❌").complete()
        return true
    }
}