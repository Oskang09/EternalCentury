package com.ec.manager.arena

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.ec.ECCore
import com.ec.config.arena.ArenaConfig
import com.ec.logger.Logger
import com.ec.manager.GlobalManager
import com.ec.model.player.ECPlayerGameState
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.config.type.MultiConfigs
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkUnloadEvent

@Component
class ArenaManager(
    @Inject("plugins/EternalCentury/arena")
    private val arenaFiles: MultiConfigs<ArenaConfig>,
) {

    companion object {

        private val ENTITY_PACKETS = arrayOf(
            PacketType.Play.Server.ENTITY_EQUIPMENT,
            PacketType.Play.Server.ANIMATION,
            PacketType.Play.Server.NAMED_ENTITY_SPAWN,
            PacketType.Play.Server.COLLECT,
            PacketType.Play.Server.SPAWN_ENTITY,
            PacketType.Play.Server.SPAWN_ENTITY_LIVING,
            PacketType.Play.Server.SPAWN_ENTITY_PAINTING,
            PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB,
            PacketType.Play.Server.ENTITY_VELOCITY,
            PacketType.Play.Server.REL_ENTITY_MOVE,
            PacketType.Play.Server.ENTITY_LOOK,
            PacketType.Play.Server.ENTITY_TELEPORT,
            PacketType.Play.Server.ENTITY_HEAD_ROTATION,
            PacketType.Play.Server.ENTITY_STATUS,
            PacketType.Play.Server.ATTACH_ENTITY,
            PacketType.Play.Server.ENTITY_METADATA,
            PacketType.Play.Server.ENTITY_EFFECT,
            PacketType.Play.Server.REMOVE_ENTITY_EFFECT,
            PacketType.Play.Server.BLOCK_BREAK_ANIMATION
        )

    }

    private lateinit var globalManager: GlobalManager
    private val arenaConfigs = mutableMapOf<String, ArenaConfig>()
    private val arenas = mutableMapOf<String, IArena>()
    private val manager = ProtocolLibrary.getProtocolManager()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        arenaFiles.getAll(true).forEach {
            val config = it.content
            arenaConfigs[config.id] = config
        }

        globalManager.events {

            PlayerJoinEvent::class
                .observable(true, EventPriority.HIGHEST)
                .doOnError(Logger.trackError("ArenaManager.PlayerJoinEvent", "error occurs in event subscribe"))
                .filter {
                    val player = globalManager.players.getByPlayer(it.player)
                    return@filter player.gameState == ECPlayerGameState.ARENA
                }
                .subscribe {
                    val player = globalManager.players.getByPlayer(it.player)
                    val arena = arenas[player.gameName]
                    if (arena == null) {
                        // fuck the player because of quiting
                        player.gameState = ECPlayerGameState.FREE
                        player.gameName = ""
                        it.player.teleportAsync(globalManager.serverConfig.teleports["old-spawn"]!!.location)
                        return@subscribe
                    }

                    arena.onReconnect(it.player)
                }

            PlayerQuitEvent::class
                .observable(true, EventPriority.LOWEST)
                .doOnError(Logger.trackError("ArenaManager.PlayerQuitEvent", "error occurs in event subscribe"))
                .filter {
                    val player = globalManager.players.getByPlayer(it.player)
                    return@filter player.gameState == ECPlayerGameState.ARENA
                }.subscribe {
                    val player = globalManager.players.getByPlayer(it.player)
                    val arena = arenas[player.gameName]
                    if (arena == null) {
                        player.gameState = ECPlayerGameState.FREE
                        player.gameName = ""
                        return@subscribe
                    }

                    if (arena.isStarted) {
                        arena.onDisconnect(it.player)
                    } else {
                        arena.onQuit(it.player)
                    }

                }

            EntityTargetEvent::class
                .observable(false, EventPriority.HIGHEST)
                .filter { it.entity.world.name == "dungeon" }
                .subscribe {
                    if (it.target is Player) {
                        val ecPlayer = globalManager.players.getByPlayer(it.target as Player)
                        if (ecPlayer.gameState == ECPlayerGameState.ARENA) {
                            val arena = globalManager.arenas.getArenaById(ecPlayer.gameName)
                            if (!arena.entities.contains(it.entity.entityId)) {
                                it.isCancelled = true
                                return@subscribe
                            }
                        }
                    }
                }

        }

        manager.addPacketListener(
            object: PacketAdapter(ECCore.instance, *ENTITY_PACKETS) {
                override fun onPacketSending(event: PacketEvent) {
                    val entityId = event.packet?.integers?.read(0)!!
                    if (
                        event.player.world.name == "dungeon" &&
                        !isVisible(event.player, entityId)
                    ) {
                        event.isCancelled = true
                    }
                }
            }
        )
    }

    fun createArena(host: Player, configId: String) {
        if (globalManager.players.getByPlayer(host).gameState != ECPlayerGameState.FREE) {
            host.sendMessage(globalManager.message.system("您目前在活动或者副本中，无法开启新的副本。"))
            return
        }

        val arena = IArena(
            globalManager,
            arenaConfigs[configId]!!,
            host,
            globalManager.serverConfig.teleports["old-spawn"]!!.location,
            globalManager.serverConfig.teleports["dungeon-lobby"]!!.location,
        )

        arenas[arena.id] = arena
    }

    fun getArenaConfigs(): List<ArenaConfig> {
        return arenaConfigs.values.toList()
    }

    fun getArenas(): List<IArena> {
        return arenas.values.toList()
    }

    fun getArenaById(id: String): IArena {
        return this.arenas[id]!!
    }

    fun removeArenaById(id: String) {
        this.arenas.remove(id)
    }

    fun showEntityToPlayers(observers: HashSet<Player>, entity: Entity) {
        manager.updateEntity(entity, observers.toList())
    }

    private fun isVisible(observer: Player, entityId: Int): Boolean {
        return try {
            val arenaId = globalManager.players.getByPlayer(observer).gameName
            val arena = arenas[arenaId]
            if (arena != null) {
                return arena.entities.contains(entityId)
            }
            return false
        } catch (e: Throwable) {
            true
        }
    }

}