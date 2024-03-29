package com.ec.manager.skill

import com.ec.manager.GlobalManager
import com.ec.model.EntityStateSkill
import dev.reactant.reactant.core.component.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
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
            Bukkit.getWorlds().map { it.entities }
                .flatten()
                .filterIsInstance<LivingEntity>()
                .parallelStream()
                .filter { it.isTicking }
                .filter { getEntitySkill(it).isNotEmpty() }
                .forEach { e ->
                    getEntitySkill(e).parallelStream().forEach { skill ->
                        skill.onTick(e, count)
                    }
                }
        }

        globalManager.events {

            EntityDeathEvent::class
                .observable(false, EventPriority.MONITOR)
                .subscribe {
                    getEntitySkill(it.entity).forEach { skill ->
                        skill.onDeath(it, it.entity)
                    }
                }

            EntityRegainHealthEvent::class
                .observable(false, EventPriority.MONITOR)
                .subscribe {
                    val entity = it.entity
                    if (entity is LivingEntity) {
                        getEntitySkill(entity).forEach { skill ->
                            skill.onRegain(it, entity)
                        }
                    }
                }

            ProjectileLaunchEvent::class
                .observable(false, EventPriority.MONITOR)
                .filter { it.entity.shooter != null }
                .subscribe {
                    val victim = it.entity.shooter!!
                    if (victim is LivingEntity)
                        getEntitySkill(victim).forEach { skill ->
                        skill.onShoot(it, victim, it.entity)
                    }
                }

            PlayerChangedWorldEvent::class
                .observable(false, EventPriority.MONITOR)
                .subscribe {
                    getEntitySkill(it.player).parallelStream().forEach { skill ->
                        skill.onChange(it.player, it.player.world)
                    }
                }

            WeatherChangeEvent::class
                .observable(false, EventPriority.MONITOR)
                .subscribe {
                    it.world.entities.filter { e -> e.isTicking }.filterIsInstance<LivingEntity>().forEach { e ->
                        getEntitySkill(e).parallelStream().forEach { skill ->
                            skill.onChange(e, it.world)
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
                    if (shooter is LivingEntity) {
                        getEntitySkill(shooter).forEach { skill ->
                            skill.onProjectileHitBlock(it, shooter, projectile, victim)
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
                    if (shooter is LivingEntity && victim is LivingEntity) {
                        getEntitySkill(shooter).forEach { skill ->
                            skill.onProjectileHitEntity(it, shooter, projectile, victim, SkillAPI.Type.ATTACK)
                        }

                        getEntitySkill(victim).forEach { skill ->
                            skill.onProjectileHitEntity(it, victim, projectile, shooter, SkillAPI.Type.DEFEND)
                        }
                    }
                }

            EntityDamageByEntityEvent::class
                .observable(false, EventPriority.MONITOR)
                .filter { it.cause != EntityDamageEvent.DamageCause.PROJECTILE }
                .subscribe {
                    val attacker = it.damager
                    val victim = it.entity
                    if (attacker is LivingEntity && victim is LivingEntity) {
                        getEntitySkill(attacker).forEach { skill -> skill.onDamage(it, attacker, victim, SkillAPI.Type.ATTACK) }
                        getEntitySkill(victim).forEach { skill -> skill.onDamage(it, victim, attacker, SkillAPI.Type.DEFEND) }
                    }
                }

        }
    }

    // Should always run asynchronously
    fun mountSkills(entity: LivingEntity, skills: List<EntityStateSkill>) {
        val allSkills = skills.mapNotNull { this.skills[it.skill]?.skill(it.level) }
        allSkills.forEach { it.onMount(entity) }
    }

    // Should always run asynchronously
    fun disposeSkills(entity: LivingEntity, skills: List<EntityStateSkill>)  {
        val allSkills = skills.mapNotNull { this.skills[it.skill]?.skill(it.level) }
        allSkills.forEach { it.onMount(entity) }
    }

    private fun getEntitySkill(entity: LivingEntity): List<SkillAPI.SkillActor> {
        val state = globalManager.states.getState(entity)
        return state.skills.mapNotNull { skills[it.skill]?.skill(it.level) }
    }

}