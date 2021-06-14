package com.ec.extension

import com.ec.ECCore
import com.ec.config.RewardConfig
import com.ec.config.ServerConfig
import com.ec.database.model.ChatType
import com.ec.extension.discord.DiscordManager
import com.ec.extension.enchantment.EnchantmentManager
import com.ec.extension.inventory.UIComponent
import com.ec.extension.inventory.UIManager
import com.ec.extension.item.ItemManager
import com.ec.extension.item.UguiProvider
import com.ec.extension.papi.PlaceholderManager
import com.ec.extension.payment.PaymentManager
import com.ec.extension.player.PlayerManager
import com.ec.extension.point.PointManager
import com.ec.extension.title.TitleManager
import com.ec.model.Emoji
import com.ec.service.EconomyService
import com.ec.service.MessageService
import com.ec.service.PermissionService
import com.ec.util.StringUtil.colorize
import com.gmail.nossr50.util.player.UserManager
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.service.spec.config.Config
import dev.reactant.reactant.service.spec.server.EventService
import dev.reactant.reactant.service.spec.server.SchedulerService
import me.oska.UniversalGUI
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import net.skinsrestorer.api.SkinsRestorerAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.inventory.meta.Damageable
import kotlin.concurrent.thread

@Component
class GlobalManager(

    // Managers
    val reflections: ReflectionManager,
    val players: PlayerManager,
    val enchantments: EnchantmentManager,
    val placeholders: PlaceholderManager,
    val titles: TitleManager,
    val points: PointManager,
    val items: ItemManager,
    val discord: DiscordManager,
    val inventory: UIManager,
    val component: UIComponent,
    val states: StateManager,
    val payments: PaymentManager,

    // Services
    var economy: EconomyService,
    var permission: PermissionService,
    val message: MessageService,

    // Libraries
    val events: EventService,
    val schedulers: SchedulerService,

    // Configurations
    @Inject("plugins/EternalCentury/server.json")
    private val serverConfigFile: Config<ServerConfig>,
): LifeCycleHook {

    lateinit var skins: SkinsRestorerAPI
    var serverConfig: ServerConfig = serverConfigFile.content

    fun runInMainThread(action: () -> Unit) {
        Bukkit.getScheduler().runTask(ECCore.instance, action)
    }

    fun runOffMainThread(action: () -> Unit) {
        thread {
            action()
        }
    }

    fun reloadServerConfig() {
        serverConfigFile.refresh().subscribe {
            serverConfig = serverConfigFile.content
        }
    }

    fun sendRewardToPlayer(rewards: List<RewardConfig>, player: Player) {
        rewards.forEach { cfg ->
            when (cfg.type.lowercase()) {
                "item" -> player.inventory.addItem(
                    if (cfg.itemId != null) items.getItemByKey(cfg.itemId)
                    else items.getItemByConfig(cfg.item!!)
                )
                "enchantment" ->
                    player.inventory.addItem(enchantments.getEnchantedBookByMap(cfg.enchantments!!))
                "command" -> cfg.commands?.map {
                    val cmd = it.replace("<player>", player.name)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
                }
            }
        }
    }

    fun mcmmoGetPlayerParty(player: Player): List<Player> {
        val mcmmoPlayer = UserManager.getPlayer(player)
        if (!mcmmoPlayer.inParty()) {
            player.sendMessage(message.system("您目前不在任何队伍。"))
            return listOf()
        }

        val party = mcmmoPlayer.party
        return party.onlineMembers

    }

    fun mcmmoPartyIsNearby(starter: Player, challenge: String): Boolean {
        val mcmmoPlayer = UserManager.getPlayer(starter)
        if (!mcmmoPlayer.inParty()) {
            starter.sendMessage(message.system("您目前不在任何队伍。"))
            return false
        }

        val party = mcmmoPlayer.party
        val nearbyMembers = party.getNearMembers(mcmmoPlayer)
        if (nearbyMembers.size != party.onlineMembers.size) {
            val messages = arrayListOf(message.system("&f&l玩家 ${starter.displayName} &f&l发起了挑战 - $challenge"))
            messages.addAll(party.onlineMembers.mapIndexed { count, member ->
                return@mapIndexed if (!nearbyMembers.contains(member)) {
                    "&f${count+1}. &c&l${Emoji.CROSS.text} &e&l玩家 ${member.displayName} 还没集合！"
                } else {
                    "&f${count+1}. &e&l玩家 ${member.displayName} 准备就绪！"
                }
            })

            party.onlineMembers.forEach {
                it.sendMessage(messages.colorize().toTypedArray())
            }
            return false
        }
        return true
    }

    fun mcmmoPartyTeleport(starter: Player, challenge: String, to: String) {
        val mcmmoPlayer = UserManager.getPlayer(starter)
        if (!mcmmoPlayer.inParty()) {
            starter.sendMessage(message.system("您目前不在任何队伍。"))
            return
        }

        val party = mcmmoPlayer.party
        val nearbyMembers = party.getNearMembers(mcmmoPlayer)
        if (nearbyMembers.size != party.onlineMembers.size) {
            val messages = arrayListOf(message.system("&f&l玩家 ${starter.displayName} &f&l发起了挑战 - &e&l${challenge}"))
            messages.addAll(party.onlineMembers.map { member ->
                return@map if (!nearbyMembers.contains(member)) {
                    "&c&l${Emoji.CROSS.text} &e&l玩家 ${member.displayName} 还没集合！"
                } else {
                    "&e&l玩家 ${member.displayName} 准备就绪！"
                }
            })

            party.onlineMembers.forEach {
                it.sendMessage(messages.toTypedArray())
            }
            return
        }

        party.onlineMembers.forEach {
            it.teleport(serverConfig.teleports[to]!!)
        }
    }

    override fun onDisable() {
        serverConfigFile.save().subscribe()
    }

    override fun onEnable() {
        val service = Bukkit.getServer().servicesManager
        permission = service.getRegistration(Permission::class.java)!!.provider as PermissionService
        economy = service.getRegistration(Economy::class.java)!!.provider as EconomyService

        permission.onInitialize(this)
        economy.onInitialize(this)
        discord.onInitialize(this)

        enchantments.onInitialize(this)
        placeholders.onInitialize(this)
        payments.onInitialize(this)
        players.onInitialize(this)
        titles.onInitialize(this)
        points.onInitialize(this)
        inventory.onInitialize(this)
        items.onInitialize(this)
        skins = SkinsRestorerAPI.getApi()


        val plugin = Bukkit.getPluginManager().getPlugin("UniversalGUI")
        if (plugin != null) {
            val ugui = plugin as UniversalGUI
            ugui.registerProvider(UguiProvider(this))
            reflections.loopModules {
                it.initialize(this)
                ugui.registerModule(it)
            }
        }

        events {

            ServerListPingEvent::class
                .observable(true, EventPriority.HIGHEST)
                .subscribe {
                    it.maxPlayers = 0
                    it.motd = "           §f§l[§5§lEC§f§l] §b§l永恒新世纪  §f§l多种玩法,多种乐趣！\n    §f§l| §c§lMCMMO §f§l| §a§l原味生存 §f§l| §7§l自制插件 §f§l| §d§l赛季玩法 §f§l|".colorize()
                }

            NPCRightClickEvent::class
                .observable(true, EventPriority.HIGHEST)
                .subscribe {
                    val player = it.clicker
                    when (it.npc.id) {
                        serverConfig.repairNpcId -> {
                            val item = player.inventory.itemInMainHand
                            if (item.hasItemMeta() && item.itemMeta is Damageable) {
                                return@subscribe inventory.displayRepair(player);
                            }
                            player.sendMessage(message.npc(it.npc, "您的物品暂时不需要修理"))
                        }

                    }
                }

        }
    }

}