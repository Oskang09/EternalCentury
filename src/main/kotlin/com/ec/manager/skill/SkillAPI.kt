package com.ec.manager.skill

import com.ec.config.arena.ArenaType
import com.ec.manager.GlobalManager
import com.ec.model.Emoji
import com.ec.model.player.ECPlayerGameState
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.*

abstract class SkillAPI(val id: String) {

    protected lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    fun skill(level: Int): SkillActor {
        return actor(SkillData(level, globalManager))
    }

    enum class Type {
        ATTACK,
        DEFEND
    }

    abstract val maxLevel: Int
    abstract val startLevel: Int
    abstract fun actor(data: SkillData): SkillActor

    data class SkillData(
        val level: Int,
        val globalManager: GlobalManager,
    )

    abstract class SkillActor(val data: SkillData) {

        fun nearbyEnemies(entity: Entity, radius: Double): List<LivingEntity> {
            val entities = entity.getNearbyEntities(radius, radius, radius).filterIsInstance<LivingEntity>()
            return when (entity is Player) {
                true -> {
                    val ecPlayer = data.globalManager.players.getByPlayer(entity)
                    return when (ecPlayer.gameState) {
                        ECPlayerGameState.FREE -> entities.filterNot { it is Player }
                        ECPlayerGameState.ACTIVITY -> when (ecPlayer.gameName) {
                            "zombie-fight" -> entities.filterNot { it is Player }
                            "nether-dg" -> entities.filterNot { it is Player }
                            "end-dg" -> entities.filterNot { it is Player }
                            else -> listOf()
                        }
                        ECPlayerGameState.ARENA -> {
                            val arena = data.globalManager.arenas.getArenaById(ecPlayer.gameName)
                            return when (arena.type) {
                                ArenaType.MOBARENA -> entities
                                    .filterNot { it is Player }
                                    .filter { data.globalManager.arenas.isVisible(entity, it.entityId) }
                            }
                        }
                    }
                }
                else -> entities.filterIsInstance<Player>()
            }
        }

        fun nearbyAllies(entity: Entity, radius: Double): List<LivingEntity> {
            val entities = entity.getNearbyEntities(radius, radius, radius).filterIsInstance<LivingEntity>()
            return when (entity is Player) {
                true -> {
                    val ecPlayer = data.globalManager.players.getByPlayer(entity)
                    return when (ecPlayer.gameState) {
                        ECPlayerGameState.FREE -> entities.filterIsInstance<Player>()
                        ECPlayerGameState.ACTIVITY -> when (ecPlayer.gameName) {
                            "zombie-fight" -> entities.filterIsInstance<Player>()
                            "nether-dg" -> entities.filterNot { it is Player }
                            "end-dg" -> entities.filterNot { it is Player }
                            else -> listOf()
                        }
                        ECPlayerGameState.ARENA -> {
                            val arena = data.globalManager.arenas.getArenaById(ecPlayer.gameName)
                            return when (arena.type) {
                                ArenaType.MOBARENA -> entities
                                    .filterIsInstance<Player>()
                                    .filter { data.globalManager.arenas.isVisible(entity, it.entityId) }
                            }
                        }
                    }
                }
                else -> entities.filterNot { it is Player }
            }
        }

        /**
         * onTick, onChange, onMount, onDispose
         * is running on async thread, any bukkit api call
         * required to run with `globalManager.runInMainThread`
         */
        open fun onTick(entity: LivingEntity, times: Int) {

        }

        open fun onMount(entity: LivingEntity) {

        }

        open fun onDispose(entity: LivingEntity) {

        }

        open fun onChange(caster: LivingEntity, world: World) {

        }

        open fun onRegain(event: EntityRegainHealthEvent, caster: LivingEntity) {

        }

        open fun onDeath(event: EntityDeathEvent, caster: LivingEntity) {

        }

        open fun onDamage(event: EntityDamageByEntityEvent, caster: LivingEntity, victim: LivingEntity, type: Type) {

        }

        open fun onShoot(event: ProjectileLaunchEvent, caster: LivingEntity, projectile: Projectile) {

        }

        open fun onProjectileHitBlock(event: ProjectileHitEvent, caster: LivingEntity, projectile: Projectile, block: Block) {

        }

        open fun onProjectileHitEntity(event: ProjectileHitEvent, caster: LivingEntity, projectile: Projectile, victim: LivingEntity, type: Type) {

        }

    }

}