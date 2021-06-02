package com.ec.extension.player

import com.ec.config.PlayerData
import com.ec.config.model.ECPlayer
import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.config.type.MultiConfigs
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.Instant
import java.util.*

@Component
class PlayerManager(

    @Inject("plugins/server-data/players")
    private val playerConfigs: MultiConfigs<PlayerData>,

) {
    private val players: MutableMap<UUID, ECPlayer> = HashMap()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

         globalManager.events {
            PlayerJoinEvent::class.observable(EventPriority.HIGHEST).subscribe { event ->
                val uuid = event.player.uniqueId
                val file = "$uuid.json"
                playerConfigs.getOrPut(file) { PlayerData(uuid, event.player.name) }
                    .subscribe { it ->
                        players[uuid] = ECPlayer(event.player, it)
                    }
            }

            PlayerQuitEvent::class.observable(EventPriority.HIGHEST).subscribe { event ->
                val uuid = event.player.uniqueId
                players.remove(uuid)?.ensureUpdate("saving player") {
                    it.content.lastOnlineAt = Instant.now().epochSecond
                }
            }
        }
    }

    private fun getByOfflinePlayer(player: OfflinePlayer): ECPlayer? {
        var file = "${player.uniqueId}.json"
        val maybe = playerConfigs.get(file)
        if (maybe.isEmpty.blockingGet()) {
            return null
        }
        return ECPlayer(null, maybe.blockingGet())
    }

    fun getByPlayer(player: Player): ECPlayer? {
        return players[player.uniqueId] ?: getByOfflinePlayer(player)
    }

}