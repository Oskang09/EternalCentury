package com.ec.extension

import com.ec.ECCore
import com.ec.config.RewardConfig
import com.ec.config.ServerConfig
import com.ec.database.*
import com.ec.extension.discord.DiscordManager
import com.ec.extension.enchantment.EnchantmentManager
import com.ec.extension.inventory.UIComponent
import com.ec.extension.inventory.UIManager
import com.ec.extension.item.ItemManager
import com.ec.extension.papi.PlaceholderManager
import com.ec.extension.title.TitleManager
import com.ec.extension.trait.TraitManager
import com.ec.extension.player.PlayerManager
import com.ec.extension.point.PointManager
import com.ec.service.*
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.service.spec.config.Config
import dev.reactant.reactant.service.spec.server.EventService
import dev.reactant.reactant.service.spec.server.SchedulerService
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.ServicePriority
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.concurrent.thread

@Component
class GlobalManager(

    // Managers
    val reflections: ReflectionManager,
    val players: PlayerManager,
    val enchantments: EnchantmentManager,
    val placeholders: PlaceholderManager,
    val titles: TitleManager,
    val traits: TraitManager,
    val points: PointManager,
    val items: ItemManager,
    val discord: DiscordManager,
    val inventory: UIManager,
    val component: UIComponent,
    val states: StateManager,

    // Services
    var economy: EconomyService,
    var permission: PermissionService,
    val message: MessageService,

    // Libraries
    val events: EventService,
    val schedulers: SchedulerService,

    // Configurations
    @Inject("plugins/server-data/server.json")
    private val serverConfigFile: Config<ServerConfig>
): LifeCycleHook {

    val serverConfig: ServerConfig = serverConfigFile.content

    fun runInMainThread(action: () -> Unit) {
        Bukkit.getScheduler().runTask(ECCore.instance, action)
    }

    fun runOffMainThread(action: () -> Unit) {
        thread {
            action()
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
        players.onInitialize(this)
        titles.onInitialize(this)
        points.onInitialize(this)
        traits.onInitialize(this)
        inventory.onInitialize(this)
        items.onInitialize(this)
    }

}