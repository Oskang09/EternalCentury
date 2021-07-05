package com.ec.manager.skill

import com.ec.manager.GlobalManager
import dev.reactant.reactant.core.component.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.weather.WeatherChangeEvent

@Component
class SkillManager {

    private val skills = mutableMapOf<String, SkillAPI>()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.reflections.loopSkills {
            val skill = it.getDeclaredConstructor().newInstance()
            skill.onInitialize(globalManager)

            skills[skill.id] = skill
        }

        globalManager.states.asyncContinuousTask(1) { count ->
            Bukkit.getWorlds().map {  it.entities }
                .flatten()
                .parallelStream()
                .filter { it.isTicking }
                .filter { extractSkillTags(it).isNotEmpty() }
                .forEach { e ->
                    extractSkillTags(e).parallelStream().forEach {
                        skills[it]?.onTick(e, count)
                    }
                }
        }

        globalManager.events {

            EntityDeathEvent::class
                .observable(false, EventPriority.MONITOR)
                .subscribe {
                    extractSkillTags(it.entity).forEach { name ->
                        skills[name]?.onDeath(it, it.entity)
                    }
                }

            EntityRegainHealthEvent::class
                .observable(false, EventPriority.MONITOR)
                .subscribe {
                    extractSkillTags(it.entity).forEach { name ->
                        skills[name]?.onRegain(it, it.entity)
                    }
                }

            ProjectileLaunchEvent::class
                .observable(false, EventPriority.MONITOR)
                .filter { it.entity.shooter != null }
                .subscribe {
                    val victim = it.entity.shooter!!
                    if (victim is Entity)
                    extractSkillTags(victim).forEach { name ->
                        skills[name]?.onShoot(it, victim, it.entity)
                    }
                }

            PlayerChangedWorldEvent::class
                .observable(false, EventPriority.MONITOR)
                .subscribe {
                    extractSkillTags(it.player).parallelStream().forEach { name ->
                        skills[name]?.onWorldChange(it.player, it.player.world)
                    }
                }

            WeatherChangeEvent::class
                .observable(false, EventPriority.MONITOR)
                .subscribe {
                    it.world.entities.filter { e -> e.isTicking }.forEach { e ->
                        extractSkillTags(e).parallelStream().forEach { name ->
                            skills[name]?.onWorldChange(e, it.world)
                        }
                    }
                }

            ProjectileHitEvent::class
                .observable(false, EventPriority.MONITOR)
                .filter { it.hitBlock != null }
                .subscribe {
                    val projectile = it.entity
                    val shooter = it.entity.shooter!!
                    val victim = it.hitBlock!!
                    if (shooter is Entity) {
                        extractSkillTags(shooter).forEach { name ->
                            skills[name]?.onProjectileHitBlock(it, shooter, projectile, victim)
                        }
                    }
                }

            ProjectileHitEvent::class
                .observable(false, EventPriority.MONITOR)
                .filter { it.hitEntity != null && it.entity.shooter != null }
                .subscribe {
                    val projectile = it.entity
                    val shooter = it.entity.shooter!!
                    val victim = it.hitEntity!!
                    if (shooter is Entity) {
                        extractSkillTags(shooter).forEach { name ->
                            skills[name]?.onProjectileHitEntity(it, shooter, projectile, victim, SkillAPI.Type.ATTACK)
                        }

                        extractSkillTags(victim).forEach { name ->
                            skills[name]?.onProjectileHitEntity(it, victim, projectile, shooter, SkillAPI.Type.DEFEND)
                        }
                    }
                }

            EntityDamageByEntityEvent::class
                .observable(false, EventPriority.MONITOR)
                .filter { it.cause != EntityDamageEvent.DamageCause.PROJECTILE }
                .subscribe {
                    val attacker = it.damager
                    val victim = it.entity
                    extractSkillTags(attacker).forEach { name -> skills[name]?.onDamage(it, attacker, victim, SkillAPI.Type.ATTACK) }
                    extractSkillTags(victim).forEach { name -> skills[name]?.onDamage(it, victim, attacker, SkillAPI.Type.DEFEND) }
                }

        }
    }

    private fun extractSkillTags(entity: Entity): List<String> {
        val tag = entity.scoreboardTags.find {  it.contains("skills@") } ?: return listOf()
        return tag.trimStart(*"skills@".toCharArray()) .split(",")
    }


}