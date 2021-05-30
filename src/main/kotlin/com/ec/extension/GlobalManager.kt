package com.ec.extension

import com.ec.config.ServerData
import com.ec.extension.enchantment.EnchantmentManager
import com.ec.extension.inventory.UIComponent
import com.ec.extension.inventory.UIManager
import com.ec.extension.papi.PlaceholderManager
import com.ec.extension.title.TitleManager
import com.ec.extension.trait.TraitManager
import com.ec.extension.player.PlayerManager
import com.ec.extension.point.PointManager
import com.ec.service.ChatService
import com.ec.service.EconomyService
import com.ec.service.LoggerService
import com.ec.service.PermissionService
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.service.spec.config.Config
import dev.reactant.reactant.service.spec.server.EventService
import dev.reactant.reactant.service.spec.server.SchedulerService

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
    val inventory: UIManager,
    val component: UIComponent,

    // Services
    val economy: EconomyService,
    val permission: PermissionService,
    val logger: LoggerService,
    val chat: ChatService,

    // Built-in
    val events: EventService,
    val schedulers: SchedulerService,

    // Configurations
    @Inject("plugins/server-data/server.json")
    private val serverConfigFile: Config<ServerData>
): LifeCycleHook {

    val serverConfig: ServerData = serverConfigFile.content

    override fun onEnable() {
        enchantments.onInitialize(this)
        placeholders.onInitialize(this)
        players.onInitialize(this)
        titles.onInitialize(this)
        points.onInitialize(this)
        traits.onInitialize(this)
        inventory.onInitialize(this)
    }

}