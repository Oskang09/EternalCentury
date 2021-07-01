package com.ec.manager

import com.ec.ECCore
import com.ec.config.ServerConfig
import com.ec.database.Mails
import com.ec.database.Players
import com.ec.database.model.Reward
import com.ec.manager.activity.ActivityManager
import com.ec.manager.crate.CrateManager
import com.ec.manager.discord.DiscordManager
import com.ec.manager.enchantment.EnchantmentManager
import com.ec.manager.inventory.UIComponent
import com.ec.manager.inventory.UIManager
import com.ec.manager.item.ItemManager
import com.ec.manager.mcmmo.McMMOManager
import com.ec.minecraft.ugui.UguiProvider
import com.ec.manager.papi.PlaceholderManager
import com.ec.manager.payment.PaymentManager
import com.ec.manager.player.PlayerManager
import com.ec.manager.wallet.WalletManager
import com.ec.manager.title.TitleManager
import com.ec.logger.Logger
import com.ec.manager.packet.PacketManager
import com.ec.service.EconomyService
import com.ec.service.MessageService
import com.ec.service.PermissionService
import com.ec.util.StringUtil.generateUniqueID
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.service.spec.config.Config
import dev.reactant.reactant.service.spec.server.EventService
import dev.reactant.reactant.service.spec.server.SchedulerService
import me.oska.UniversalGUI
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import net.skinsrestorer.api.SkinsRestorerAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import kotlin.concurrent.thread

@Component
class GlobalManager(

    // Managers
    val reflections: ReflectionManager,
    val players: PlayerManager,
    val enchantments: EnchantmentManager,
    val placeholders: PlaceholderManager,
    val titles: TitleManager,
    val wallets: WalletManager,
    val items: ItemManager,
    val discord: DiscordManager,
    val inventory: UIManager,
    val component: UIComponent,
    val states: StateManager,
    val payments: PaymentManager,
    val crates: CrateManager,
    val mcmmo: McMMOManager,
    val activity: ActivityManager,
    val packet: PacketManager,

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

    private val mapper = jacksonObjectMapper()
    lateinit var skins: SkinsRestorerAPI
    var serverConfig: ServerConfig = serverConfigFile.content

    fun getPlayerOriginTexture(skinName: String): String? {
        val profile = skins.getSkinData(skinName)
        return profile?.value
    }

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

    fun saveServerConfig() {
        serverConfigFile.save().subscribe {
            serverConfig = serverConfigFile.content
        }
    }

    fun sendRewardToPlayer(player: Player, rewards: List<Reward>) {
        sendRewardToPlayer(player, *rewards.toTypedArray())
    }

    fun sendRewardToPlayer(player: Player, vararg rewards: Reward) {
        val itemRewards = mutableListOf<ItemStack>()

        rewards.forEach { cfg ->
            when (cfg.type.lowercase()) {
                "item" -> itemRewards.add(
                    if (cfg.itemId != null) items.getItemByKey(cfg.itemId)
                    else items.getItem(cfg.item!!)
                )
                "enchantment" ->
                    itemRewards.add(enchantments.getEnchantedBookByMap(cfg.enchantments!!))
                "command" -> cfg.commands?.map {
                    val cmd = it.replace("<player>", player.name)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
                }
            }
        }

        givePlayerItem(player.name, itemRewards)
    }

    fun givePlayerItem(name: String, items: List<ItemStack>) {
        val itemLeft = items.toMutableList()
        val player = Bukkit.getPlayerExact(name)
        if (player != null && player.isOnline) {
            while (itemLeft.isNotEmpty()) {
                if (player.inventory.firstEmpty() == -1) {
                    break
                }

                player.inventory.addItem(itemLeft.removeLast())
            }
        }

        if (itemLeft.size > 0) {
            val ecPlayer = players.getByPlayerName(name)
            if (ecPlayer != null) {
                transaction {
                    Mails.insert {
                        it[id] = "".generateUniqueID()
                        it[playerId] = ecPlayer[Players.id]
                        it[title] = "&f[&5系统&f] &f未领取奖励"
                        it[content] = mutableListOf("&f因为您离线或者背包满了所以物品寄托在了信箱。")
                        it[item] = arrayListOf(*itemLeft.toTypedArray())
                        it[rewards] = arrayListOf()
                        it[isRead] = false
                        it[createdAt] = Instant.now().epochSecond
                    }
                }
            }
        }
    }

    override fun onDisable() {
        serverConfigFile.save().subscribe()
    }

    override fun onEnable() {
        val service = Bukkit.getServer().servicesManager

        permission = service.getRegistration(Permission::class.java)!!.provider as PermissionService
        permission.onInitialize(this)

        economy = service.getRegistration(Economy::class.java)!!.provider as EconomyService
        economy.onInitialize(this)

        discord.onInitialize(this)
        enchantments.onInitialize(this)
        placeholders.onInitialize(this)
        payments.onInitialize(this)
        players.onInitialize(this)
        titles.onInitialize(this)
        wallets.onInitialize(this)
        inventory.onInitialize(this)
        items.onInitialize(this)
        crates.onInitialize(this)
        states.onInitialize(this)
        activity.onInitialize(this)
        mcmmo.onInitialize(this)
        packet.onInitialize(this)
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
                .doOnError(Logger.trackError("GlobalManager.ServerListPingEvent", "error occurs in event subscriber"))
                .subscribe {
                    it.maxPlayers = 0
                    it.motd(
                        net.kyori.adventure.text.Component.text("           §f§l[§5§lEC§f§l] §b§l永恒新世纪  §f§l多种玩法,多种乐趣！\n    §f§l| §c§lMCMMO §f§l| §a§l原味生存 §f§l| §7§l自制插件 §f§l| §d§l赛季玩法 §f§l|")
                    )
                }

        }
    }

}