package com.ec.manager.discord

import club.minnced.jda.reactor.ReactiveEventManager
import club.minnced.jda.reactor.on
import club.minnced.jda.reactor.onMessage
import com.ec.ECCore
import com.ec.database.Players
import com.ec.database.enums.ChatType
import com.ec.logger.Logger
import com.ec.manager.GlobalManager
import com.ec.model.ObservableObject
import com.ec.model.player.ECPlayerAuthState
import com.ec.util.RandomUtil
import com.ec.util.StringUtil.generateUniqueID
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.papermc.paper.event.player.AsyncChatEvent
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
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime

@dev.reactant.reactant.core.component.Component
class DiscordManager: LifeCycleHook {
    private lateinit var globalManager: GlobalManager
    private lateinit var jda: JDA
    private lateinit var guild: Guild
    private lateinit var newbieRole: Role
    private lateinit var playerRole: Role
    private lateinit var verifyObservableObject: ObservableObject<MessageReactionAddEvent>
    private lateinit var annoucementChannel: TextChannel

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        val nameRegex = Regex("^\\w{3,16}\$", options = setOf(RegexOption.IGNORE_CASE))
        val manager = ReactiveEventManager()

        jda = JDABuilder.createDefault(globalManager.serverConfig.discord.token)
            .setEnabledIntents(EnumSet.allOf(GatewayIntent::class.java))
            .setEventManager(manager)
            .build()
            .awaitReady()

        jda.presence.setPresence(OnlineStatus.ONLINE, Activity.playing("Minecraft"), false)

        guild = jda.getGuildById(globalManager.serverConfig.discord.guild)!!
        newbieRole = guild.getRoleById(globalManager.serverConfig.discord.newbieRole)!!
        playerRole = guild.getRoleById(globalManager.serverConfig.discord.playerRole)!!
        annoucementChannel = guild.getTextChannelById(globalManager.serverConfig.discord.chatAnnouncement)!!

        globalManager.events {

            AsyncChatEvent::class
                .observable(false, EventPriority.LOWEST)
                .doOnError(Logger.trackError("DiscordManager.AsyncChatEvent", "error occurs in event subscriber"))
                .subscribe {
                    val ecPlayer = globalManager.players.getByPlayer(it.player)
                    if (ecPlayer.state != ECPlayerAuthState.AUTHENTICATED) {
                        it.isCancelled = true
                        return@subscribe
                    }

                    val sender = it.player
                    val chatType = ChatType.GLOBAL

                    val prefix = globalManager.message.playerChatPrefix(chatType)
                    it.renderer { source, _, message, _ ->
                        prefix.append("&r ".toComponent()).append(source.displayName()).append("&r : ".toComponent()).append(message)
                    }

                    val ignored = globalManager.players.getByPlayer(sender).database[Players.ignoredPlayers]
                    it.viewers().removeIf { p ->
                        if (p !is Player) return@removeIf false
                        return@removeIf ignored.contains(p.uniqueId.toString())
                    }
                }
        }

        val embed = EmbedBuilder();
        embed.setColor(Color.GREEN)
        embed.setThumbnail("https://firebasestorage.googleapis.com/v0/b/eternal-century.appspot.com/o/logo.jpg?alt=media")
        embed.setAuthor("永恒新世纪 Eternal Century ", "https://minecraft.oskadev.com", "https://firebasestorage.googleapis.com/v0/b/eternal-century.appspot.com/o/logo.jpg?alt=media")
        embed.setTitle("伺服器状态 - 在线 ( Online )")
        embed.setDescription("新来的玩家可以到 <#${globalManager.serverConfig.discord.ruleChannel}> 频道，通过随便一个Reaction同意并接受规则后才能进行注册。")
        embed.addField("地区配置", "", true)
        embed.addField("人数限制", "50", true)
        embed.addField("目前版本", ECCore.VERSION, true)
        embed.addField("伺服DC", "https://discord.gg/7rfSfwQBct", true)
        embed.addField("伺服IP", "eternalcentury.oskadev.com", true)
        embed.setFooter("伺服器咨询")
        embed.setTimestamp(Instant.now())

        if (globalManager.serverConfig.discord.infoMessage == "") {
            globalManager.serverConfig.discord.infoMessage = guild.getTextChannelById(851153973960114178)!!.sendMessage(embed.build()).complete().id
        } else {
            val serverStatusMessage = guild.getTextChannelById(globalManager.serverConfig.discord.infoChannel)!!
                .retrieveMessageById(globalManager.serverConfig.discord.infoMessage).complete()
            globalManager.serverConfig.discord.infoMessage = serverStatusMessage.editMessage(embed.build()).complete().id
        }

        verifyObservableObject = ObservableObject(jda.on())
        verifyObservableObject.subscribe({
            it.member?.roles?.contains(newbieRole) != true &&
            it.messageId == globalManager.serverConfig.discord.ruleMessage
        }) {
            guild.addRoleToMember(it.member!!.idLong, newbieRole).complete()
        }

        jda.getTextChannelById(globalManager.serverConfig.discord.registerChannel)!!
            .onMessage()
            .doOnError(Logger.trackError("DiscordManager.REGISTER_CHANNEL", "error occurs in discord event"))
            .doOnNext { it.message.delete().complete() }
            .filter { !it.message.author.isBot && globalManager.players.getByDiscordTag(it.message.author.asTag) == null }
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
                        data[createdAt] = Instant.now().epochSecond
                        data[lastOnlineAt] = Instant.now().epochSecond
                        data[enchantmentRandomSeed] = RandomUtil.randomInteger(99999)
                        data[skins] = mutableListOf()
                        data[permissions] = mutableListOf()
                        data[blockedTeleport] = mutableListOf()
                        data[ignoredPlayers] = mutableListOf()
                        data[skinLimit] = 1
                        data[plotLimit] = 1
                        data[auctionLimit] = 1
                        data[homeLimit] = 1
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
        embed.setThumbnail("https://firebasestorage.googleapis.com/v0/b/eternal-century.appspot.com/o/logo.jpg?alt=media")
        embed.setAuthor("永恒新世纪 Eternal Century ", "https://minecraft.oskadev.com", "https://firebasestorage.googleapis.com/v0/b/eternal-century.appspot.com/o/logo.jpg?alt=media")
        embed.setTitle("伺服器状态 - 离线 ( Offline )")
        embed.setDescription("伺服器离线中，请等待伺服器上线后再申请账号。")
        embed.addField("地区配置", "", true)
        embed.addField("人数限制", "50", true)
        embed.addField("目前版本", ECCore.VERSION, true)
        embed.addField("伺服DC", "https://discord.gg/7rfSfwQBct", true)
        embed.addField("伺服IP", "eternalcentury.oskadev.com", true)
        embed.setFooter("伺服器咨询")
        embed.setTimestamp(Instant.now())

        verifyObservableObject.dispose()

        val serverStatusMessage = guild.getTextChannelById(851153973960114178)!!.retrieveMessageById(globalManager.serverConfig.discord.infoMessage).complete()
        globalManager.serverConfig.discord.infoMessage = serverStatusMessage.editMessage(embed.build()).complete().id

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

        message.addReaction("✅").queue()
        message.addReaction("❌").queue()
        return true
    }

    fun broadcast(message: String, item: ItemStack? = null) {
        var discordMessage = message
        var component = globalManager.message.broadcast("")
        if (message.contains("%item%") && item != null) {
            component = component.append(Component.text(message[0]))
            val name = item.itemMeta?.displayName() ?: item.displayName()
            val hoverEvent = Bukkit.getServer().itemFactory.asHoverEvent(item) { return@asHoverEvent it }
            component = component.append(Component.text("&7")
                .append(name)
                .append("&7]&r".toComponent())
                .hoverEvent(hoverEvent)
            )
            component = component.append(Component.text(message[1]))
            discordMessage = message.replace("%item%", " ${globalManager.message.plain(name)}(游戏内查看) ")
        } else {
            component = component.append(message.toComponent())
        }

        annoucementChannel.sendMessage(discordMessage).queue()
        Bukkit.getOnlinePlayers()
            .parallelStream()
            .forEach { p ->
                p.sendMessage(component)
            }
    }
}