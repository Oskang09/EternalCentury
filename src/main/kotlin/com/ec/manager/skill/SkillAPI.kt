package com.ec.manager.skill

import com.ec.manager.GlobalManager
import com.ec.model.Emoji
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
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

    data class SkillData(val level: Int)

    abstract class SkillActor(data: SkillData) {

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