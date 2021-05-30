package com.ec.extension.player

import com.ec.config.PlayerData
import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
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

    @Inject("plugins/server-data/players")
    private val playerConfigs: MultiConfigs<PlayerData>,

) {
    private val players: MutableMap<UUID, ECPlayer> = HashMap()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
//
//         globalManager.events {
//            PlayerJoinEvent::class.observable(EventPriority.HIGHEST).subscribe { event ->
//                val uuid = event.player.uniqueId
//                val file = "$uuid.json"
//                playerConfigs.getOrDefault(file){ PlayerData(uuid) }
//                    .subscribe { it ->
//                        players[uuid] = ECPlayer(event.player, it)
//                    }
//            }
//
//            PlayerQuitEvent::class.observable(EventPriority.HIGHEST).subscribe { event ->
//                val uuid = event.player.uniqueId
//                players.remove(uuid)?.save()
//            }
//        }
    }

    fun getByPlayer(player: Player): ECPlayer? {
        return players[player.uniqueId]
    }

}