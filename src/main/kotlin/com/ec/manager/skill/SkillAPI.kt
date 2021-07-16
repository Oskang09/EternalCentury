package com.ec.manager.skill

import com.ec.config.arena.ArenaType
import com.ec.manager.GlobalManager
import com.ec.model.Emoji
import com.ec.model.player.ECPlayerGameState
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.*

abstract class SkillAPI(val id: String) {

    protected lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    enum class Type {
        ATTACK,
        DEFEND
    }

    abstract val description: List<String>
    abstract val emoji: Emoji
    abstract val display: String
    abstract val maxLevel: Int
    abstract val startLevel: Int
    abstract fun isSupportedMaterial(): List<Material>
    abstract fun skill(level: Int): SkillActor

    data class SkillData(
        val level: Int,
        val globalManager: GlobalManager,
    )

    abstract class SkillActor(val data: SkillData) {

        fun nearbyEnemies(entity: Entity, radius: Double): List<Entity> {
            val entities = entity.getNearbyEntities(radius, radius, radius)
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

        fun nearbyAllies(entity: Entity, radius: Double): List<Entity> {
            val entities = entity.getNearbyEntities(radius, radius, radius)
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
         * Ticking & WorldChange ( included WeatherChange )
         * is running on async thread, any bukkit api call
         * required to run with `globalManager.runInMainThread`
         */
        open fun onTick(entity: Entity, times: Int) {

        }

        open fun onChange(caster: Entity, world: World) {

        }

        open fun onRegain(event: EntityRegainHealthEvent, caster: Entity) {

        }

        open fun onDeath(event: EntityDeathEvent, caster: Entity) {

        }

        open fun onDamage(event: EntityDamageByEntityEvent, caster: Entity, victim: Entity, type: Type) {

        }

        open fun onShoot(event: ProjectileLaunchEvent, caster: Entity, projectile: Projectile) {

        }

        open fun onProjectileHitBlock(event: ProjectileHitEvent, caster: Entity, projectile: Projectile, block: Block) {

        }

        open fun onProjectileHitEntity(event: ProjectileHitEvent, caster: Entity, projectile: Projectile, victim: Entity, type: Type) {

        }

    }

}