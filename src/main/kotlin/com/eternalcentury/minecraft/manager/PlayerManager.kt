package com.eternalcentury.minecraft.manager

import com.eternalcentury.config.PlayerData
import com.eternalcentury.minecraft.player.ECPlayer
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.component.lifecycle.LifeCycleHook
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.config.type.MultiConfigs
import dev.reactant.reactant.service.spec.server.EventService
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.collections.HashMap

@Component
class PlayerManager(
    private val eventService: EventService,
    @Inject("plugins/server-data/players")
    private val playerConfigs: MultiConfigs<PlayerData>
): LifeCycleHook {
    private val players: MutableMap<UUID, ECPlayer> = HashMap()

    override fun onEnable() {

        eventService {

            PlayerJoinEvent::class.observable(EventPriority.HIGHEST).subscribe { event ->
                val uuid = event.player.uniqueId
                val file = "$uuid.json"
                playerConfigs.getOrDefault(file){ PlayerData(uuid) }
                    .subscribe { it ->
                        players[uuid] = ECPlayer(event.player, it)
                    }
            }

            PlayerQuitEvent::class.observable(EventPriority.HIGHEST).subscribe { event ->
                val uuid = event.player.uniqueId
                players.remove(uuid)?.config?.save()
            }
        }

    }

    fun getByPlayer(player: Player): ECPlayer? {
        return players[player.uniqueId]
    }

}