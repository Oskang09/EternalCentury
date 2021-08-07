package com.ec.minecraft.skill

import com.ec.manager.skill.SkillAPI
import com.ec.util.LocationUtil.handLocation
import org.bukkit.entity.LivingEntity
import xyz.xenondevs.particle.ParticleBuilder
import xyz.xenondevs.particle.ParticleEffect

class LiXingHuo: SkillAPI("LiXingHuo") {

    private val baseParticle = ParticleBuilder(ParticleEffect.FLAME)

    override val maxLevel = 3
    override val startLevel = 1

    override fun actor(data: SkillData): SkillActor {
        return object: SkillActor(data) {

            override fun onTick(entity: LivingEntity, times: Int) {

                if (times.mod(2) == 0) {
                    baseParticle
                        .setLocation(entity.handLocation())
                        .setAmount(30)
                        .display()
                }

                if (times.mod(5) == 0) {
                    nearbyEnemies(entity, 5.0).forEach {
                        it.damage(2.0 * data.level, entity)
                        baseParticle.setLocation(it.location).display()
                    }
                }

            }

        }
    }
}