package com.ec.extension.discord

import club.minnced.jda.reactor.ReactiveEventManager
import club.minnced.jda.reactor.on
import club.minnced.jda.reactor.onMessage
import com.ec.ECCore
import com.ec.database.Players
import com.ec.database.model.ChatType
import com.ec.database.model.economy.EconomyInfo
import com.ec.database.model.point.PointInfo
import com.ec.extension.GlobalManager
import com.ec.logger.Logger
import com.ec.model.ObservableObject
import com.ec.model.player.ECPlayerState
import com.ec.util.ModelUtil.toDisplay
import com.ec.util.RandomUtil
import com.ec.util.StringUtil.colorize
import com.ec.util.StringUtil.generateUniqueID
import de.tr7zw.nbtapi.NBTItem
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import io.javalin.core.util.RouteOverviewUtil.metaInfo
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.time.Instant
import java.util.*
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

        jda = JDABuilder.createDefault(globalManager.serverConfig.discord.token)
            .setEnabledIntents(EnumSet.allOf(GatewayIntent::class.java))
            .setEventManager(manager)
            .build()
            .awaitReady()

        jda.presence.setPresence(OnlineStatus.ONLINE, Activity.playing("Minecraft"), false)

        guild = jda.getGuildById(globalManager.serverConfig.discord.guild)!!
        newbieRole = guild.getRoleById(globalManager.serverConfig.discord.newbieRole)!!
        playerRole = guild.getRoleById(globalManager.serverConfig.discord.playerRole)!!

        mutableMapOf(
            ChatType.GLOBAL to globalManager.serverConfig.discord.chatGlobal,
            ChatType.MCMMO to globalManager.serverConfig.discord.chatMcmmo,
            ChatType.SURVIVAL to globalManager.serverConfig.discord.chatSurvival,
            ChatType.PVPVE to globalManager.serverConfig.discord.chatPvpve,
            ChatType.REDSTONE to globalManager.serverConfig.discord.chatRedstone,
            ChatType.ANNOUNCEMENT to globalManager.serverConfig.discord.chatAnnouncement,
        ).forEach { (chatType, channelId) ->
            channels[chatType] = guild.getTextChannelById(channelId)!!
        }

        globalManager.events {

            AsyncPlayerChatEvent::class
                .observable(true, EventPriority.LOWEST)
                .doOnError(Logger.trackError("DiscordManager.AsyncPlayerChatEvent", "error occurs in event subscriber"))
                .filter { it.isAsynchronous }
                .subscribe {
                    it.isCancelled = true
                    if (globalManager.players.getByPlayer(it.player).state != ECPlayerState.AUTHENTICATED) {
                        return@subscribe
                    }

                    val sender = it.player
                    var message = it.message
                    val chatType = when {
                        message.startsWith("@party ") -> {
                            message = message.replace("@party ", "")
                            ChatType.PARTY
                        }
                        message.startsWith("@mcmmo ") -> {
                            message = message.replace("@mcmmo ", "")
                            ChatType.MCMMO
                        }
                        message.startsWith("@survival ") -> {
                            message = message.replace("@survival ", "")
                            ChatType.SURVIVAL
                        }
                        message.startsWith("@pvpve") -> {
                            message = message.replace("@survival ", "")
                            ChatType.PVPVE
                        }
                        message.startsWith("@redstone") -> {
                            message = message.replace("@redstone ", "")
                            ChatType.REDSTONE
                        }
                        else -> ChatType.GLOBAL
                    }

                    val component = ComponentBuilder(globalManager.message.playerChat(sender, chatType, ""))
                    if (message.contains("%item%")) {
                        val messages = message.split("%item%")
                        val item = sender.inventory.itemInMainHand
                        if (item.type == Material.AIR) {
                            sender.sendMessage(globalManager.message.system("你手上没有物品，无法发送到聊天窗！"))
                            return@subscribe
                        }

                        val display = TextComponent(("&7[" + (item.itemMeta?.displayName ?: item.type.name) + "]&r").colorize())
                        display.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_ITEM, ComponentBuilder(NBTItem.convertItemtoNBT(item).toString()).create())

                        component.append(messages[0])
                        component.append(display)
                        component.append(messages[1])
                        message = message.replace("%item%", " ${display}(游戏内查看) ")
                    } else {
                        component.append(message)
                    }

                    if (chatType != ChatType.PARTY) {
                        channels[chatType]!!.sendMessage(sender.displayName + " : " + message).queue()

                        Bukkit.getOnlinePlayers()
                            .parallelStream()
                            .filter { p -> !globalManager.players.getByPlayer(p).database[Players.ignoredPlayers].contains(sender.name) }
                            .filter { p -> globalManager.players.getByPlayer(p).database[Players.channels].contains(chatType) }
                            .forEach { p ->
                                p.spigot().sendMessage(ChatMessageType.CHAT, *component.create())
                            }
                    } else {
                        globalManager.mcmmoGetPlayerParty(sender).forEach { p ->
                            p.spigot().sendMessage(ChatMessageType.CHAT, *component.create())
                        }
                    }
                }
        }

        val embed = EmbedBuilder();
        embed.setColor(Color.GREEN)
        embed.setThumbnail("https://ngrok.oskadev.com/file/mc-logo")
        embed.setAuthor("永恒新世纪 Eternal Century ", "https://minecraft.oskadev.com", "https://ngrok.oskadev.com/file/mc-logo")
        embed.setTitle("伺服器状态 - 在线 ( Online )")
        embed.setDescription("新来的玩家可以到 \'伺服规则\' 频道，通过随便一个Reaction同意并接受规则后才能进行注册。")
        embed.addField("地区配置", "", true)
        embed.addField("人数限制", "50", true)
        embed.addField("目前版本", ECCore.VERSION, true)
        embed.addField("伺服DC", "https://discord.gg/7rfSfwQBct", true)
        embed.addField("伺服IP", "survival.oskadev.com", true)
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
        verifyObservableObject.subscribe({ it.member?.roles?.contains(newbieRole) == true && it.messageId == globalManager.serverConfig.discord.ruleMessage }) {
            guild.addRoleToMember(it.member!!.idLong, newbieRole).complete()
        }

        jda.getTextChannelById(globalManager.serverConfig.discord.commandChannel)!!
            .onMessage()
            .doOnError(Logger.trackError("DiscordManager.COMMAND_CHANNEL", "error occurs in discord event"))
            .subscribe {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.message.contentRaw)
            }

        jda.getTextChannelById(globalManager.serverConfig.discord.registerChannel)!!
            .onMessage()
            .doOnError(Logger.trackError("DiscordManager.REGISTER_CHANNEL", "error occurs in discord event"))
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
                        data[createdAt] = Instant.now().epochSecond
                        data[lastOnlineAt] = Instant.now().epochSecond
                        data[enchantmentRandomSeed] = RandomUtil.randomInteger(99999)
                        data[skins] = mutableListOf()
                        data[balance] = EconomyInfo()
                        data[points] = PointInfo()
                        data[permissions] = mutableListOf()
                        data[blockedTeleport] = mutableListOf()
                        data[ignoredPlayers] = mutableListOf()
                        data[channels] = ChatType.values().toMutableList()

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
        embed.addField("目前版本", ECCore.VERSION, true)
        embed.addField("伺服DC", "https://discord.gg/7rfSfwQBct", true)
        embed.addField("伺服IP", "survival.oskadev.com", true)
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
        val component = ComponentBuilder(globalManager.message.broadcast(""))
        if (message.contains("%item%") && item != null) {
            val messages = message.split("%item%")
            val display = TextComponent(("&7[" + (item.itemMeta?.displayName ?: item.type.name) + "]&r").colorize())
            display.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_ITEM, ComponentBuilder(NBTItem.convertItemtoNBT(item).toString()).create())

            component.append(messages[0])
            component.append(display)
            component.append(messages[1])
            discordMessage = message.replace("%item%", " ${display}(游戏内查看) ")
        } else {
            component.append(message)
        }

        channels[ChatType.ANNOUNCEMENT]!!.sendMessage(discordMessage).queue()
        Bukkit.getOnlinePlayers()
            .parallelStream()
            .filter { p -> globalManager.players.getByPlayer(p).database[Players.channels].contains(ChatType.ANNOUNCEMENT) }
            .forEach { p ->
                p.spigot().sendMessage(ChatMessageType.CHAT, *component.create())
            }
    }
}