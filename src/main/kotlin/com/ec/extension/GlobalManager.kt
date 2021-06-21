package com.ec.extension

import com.ec.ECCore
import com.ec.config.ServerConfig
import com.ec.database.model.Reward
import com.ec.extension.crate.CrateManager
import com.ec.extension.discord.DiscordManager
import com.ec.extension.enchantment.EnchantmentManager
import com.ec.extension.inventory.UIComponent
import com.ec.extension.inventory.UIManager
import com.ec.extension.item.ItemManager
import com.ec.extension.mcmmo.McMMOManager
import com.ec.minecraft.ugui.UguiProvider
import com.ec.extension.papi.PlaceholderManager
import com.ec.extension.payment.PaymentManager
import com.ec.extension.player.PlayerManager
import com.ec.extension.point.PointManager
import com.ec.extension.title.TitleManager
import com.ec.logger.Logger
import com.ec.model.Emoji
import com.ec.model.skin.SkinProfile
import com.ec.service.EconomyService
import com.ec.service.MessageService
import com.ec.service.PermissionService
import com.ec.util.StringUtil.colorize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
    val crates: CrateManager,
    val mcmmo: McMMOManager,

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

    fun sendRewardToPlayer(player: Player, rewards: List<Reward>) {
        sendRewardToPlayer(player, *rewards.toTypedArray())
    }

    fun sendRewardToPlayer(player: Player, vararg rewards: Reward) {
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
        crates.onInitialize(this)
        states.onInitialize(this)
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
                    it.motd = "           §f§l[§5§lEC§f§l] §b§l永恒新世纪  §f§l多种玩法,多种乐趣！\n    §f§l| §c§lMCMMO §f§l| §a§l原味生存 §f§l| §7§l自制插件 §f§l| §d§l赛季玩法 §f§l|".colorize()
                }

        }
    }

}