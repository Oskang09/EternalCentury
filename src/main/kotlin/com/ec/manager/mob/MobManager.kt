package com.ec.manager.mob

import com.ec.config.mobs.MobConfig
import com.ec.manager.GlobalManager
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extra.config.type.MultiConfigs
import org.bukkit.entity.Entity
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.loot.LootContext

@Component
class MobManager(
    @Inject("plugins/EternalCentury/mobs")
    private val mobsFiles: MultiConfigs<MobConfig>
) {
    private val mobs = mutableMapOf<String, IEntity>()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        mobsFiles.getAll(true).subscribe {
            val config = it.content
            mobs[config.id] = IEntity(globalManager, config)
        }

        globalManager.events {
            EntityDeathEvent::class
                .observable(true, EventPriority.LOWEST)
                .filter { isCustomMob(it.entity) }
                .subscribe {
                    val mob = getEntityMobId(it.entity)
                    it.droppedExp = mob.config.xp

                    it.drops.clear()
                    it.drops.addAll(mob.getDrops(it))
                }
        }
    }

    fun isCustomMob(entity: Entity): Boolean {
        val id = entity.scoreboardTags.find { it.startsWith("mobId@") }
        return id != null
    }

    fun getEntityMobId(entity: Entity): IEntity {
        val id = entity.scoreboardTags.find { it.startsWith("mobId@") }!!
        return mobs[id.trimStart(*"mobId@".toCharArray())]!!
    }

    fun getMobById(id: String): IEntity {
        return mobs[id]!!
    }

}