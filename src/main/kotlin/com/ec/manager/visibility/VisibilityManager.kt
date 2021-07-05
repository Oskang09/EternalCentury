package com.ec.manager.visibility

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.ec.ECCore
import com.ec.manager.GlobalManager
import dev.reactant.reactant.core.component.Component
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkUnloadEvent

@Component
class VisibilityManager {

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

    private val observerEntities = mutableMapOf<Int, HashSet<Int>>()
    private lateinit var globalManager: GlobalManager
    private val manager = ProtocolLibrary.getProtocolManager()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        manager.addPacketListener(
            object: PacketAdapter(ECCore.instance, *ENTITY_PACKETS) {
                override fun onPacketSending(event: PacketEvent) {
                    val entityId = event.packet?.integers?.read(0)!!
                    if (isHidden(event.player!!, entityId)) {
                        event.isCancelled = true
                    }
                }
            }
        )

        globalManager.events {

            PlayerQuitEvent::class
                .observable(true, EventPriority.HIGHEST)
                .subscribe {
                    observerEntities.remove(it.player.entityId)
                }

            EntityDeathEvent::class
                .observable(true, EventPriority.HIGHEST)
                .subscribe { event ->
                    val entityId = event.entity.entityId
                    observerEntities.values.forEach {
                        it.remove(entityId)
                    }
                }

            ChunkUnloadEvent::class
                .observable(true, EventPriority.HIGHEST)
                .subscribe { evt ->
                    val ids = evt.chunk.entities.map { it.entityId }
                    observerEntities.values.forEach {
                        it.removeIf { id -> ids.contains(id) }
                    }
                }

        }
    }

    fun showEntity(observer: Player, entity: Entity): Boolean {
        val hiddenBefore = !visibleTo(observer, entity)
        if (manager != null && hiddenBefore) {
            manager.updateEntity(entity, listOf(observer))
        }
        return hiddenBefore
    }

    fun hideEntity(observer: Player, entity: Entity): Boolean {
        val visibleBefore = hideTo(observer, entity)
        if (visibleBefore) {
            val destroyEntity = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
            destroyEntity.integerArrays.write(0, intArrayOf(entity.entityId))
            manager.sendServerPacket(observer, destroyEntity)
        }
        return visibleBefore
    }

    fun isHidden(observer: Player, entity: Entity): Boolean {
        return isHidden(observer, entity.entityId)
    }

    private fun visibleTo(observer: Player, entity: Entity): Boolean {
        return observerEntities.getOrPut(observer.entityId) { hashSetOf() }.remove(entity.entityId)
    }

    private fun hideTo(observer: Player, entity: Entity): Boolean {
        return observerEntities.getOrPut(observer.entityId) { hashSetOf() }.add(entity.entityId)
    }

    private fun isHidden(observer: Player, entityId: Int): Boolean {
        return observerEntities.getOrPut(observer.entityId) { hashSetOf() }.contains(entityId)
    }
}

