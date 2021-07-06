package com.ec.manager.arena

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.ec.ECCore
import com.ec.config.arena.ArenaConfig
import com.ec.manager.GlobalManager
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.config.type.MultiConfigs
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
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

    fun getArenaById(id: String): IArena {
        return this.arenas[id]!!
    }

    fun removeArenaById(id: String) {
        this.arenas.remove(id)
    }

    private fun isVisible(observer: Player, entityId: Int): Boolean {
        val arenaId = globalManager.players.getByPlayer(observer).gameName
        val arena = arenas[arenaId]
        if (arena != null) {
            return arena.entities.contains(entityId)
        }
        return false
    }

}