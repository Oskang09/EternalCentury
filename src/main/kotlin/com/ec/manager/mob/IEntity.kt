package com.ec.manager.mob

import com.ec.ECCore
import com.ec.config.mobs.MobConfig
import com.ec.manager.GlobalManager
import com.ec.model.EntityState
import com.ec.util.ModelUtil.toState
import com.ec.util.StringUtil.toComponent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootContext
import org.bukkit.loot.LootTable
import org.bukkit.loot.Lootable
import java.util.*
import kotlin.math.pow

class IEntity(val globalManager: GlobalManager, val config: MobConfig) {

    private var lootTable = object: LootTable {

        private val dropTable = mutableMapOf<ItemStack, Int>()

        init {
            config.loots.forEach {
                val itemstack = when {
                    it.item != null -> globalManager.items.getItem(it.item)
                    it.itemId != "" -> globalManager.items.getItemById(it.itemId)
                    else -> ItemStack(Material.AIR)
                }
                dropTable[itemstack] = it.base
            }
        }

        fun getDrops(random: Random, context: LootContext): List<ItemStack> {
            return dropTable.map {
                var baseDrops = random.nextInt(it.value)

                val lootLevel = context.lootingModifier.toDouble()
                if (lootLevel > 0) {
                    baseDrops += ((lootLevel.pow(0.6) + 2) / 2).toInt()
                }

                val luckLevel = context.luck.toDouble()
                if (luckLevel > 0) {
                    baseDrops += (luckLevel.pow(1.2) / 2).toInt()
                }

                val drop = it.key
                drop.add(baseDrops)
                return@map drop
            }
        }

        override fun getKey(): NamespacedKey {
            return NamespacedKey(ECCore.instance, config.id)
        }

        override fun populateLoot(random: Random, context: LootContext): MutableCollection<ItemStack> {
            return getDrops(random, context).toMutableList()
        }

        override fun fillInventory(inventory: Inventory, random: Random, context: LootContext) {
            val drops = getDrops(random, context)
            if (inventory.holder is Player) {
                globalManager.givePlayerItem((inventory.holder as Player).name, drops)
            }
        }

    }

    fun getDrops(it: EntityDeathEvent): MutableCollection<ItemStack> {
        var looting = 0
        if (it.entity.killer is Player) {
            val enchantment =globalManager.enchantments.getEntityEnchantments(it.entity.killer!!)
            val key = enchantment.keys.find { it.origin == Enchantment.LOOT_BONUS_MOBS }
            if (key != null) {
                looting = enchantment[key]!!
            }
        }

        val context = LootContext.Builder(it.entity.location)
        context.lootedEntity(it.entity)
        context.killer(it.entity.killer)
        context.lootingModifier(looting)
        context.luck((it.entity.killer?.getAttribute(Attribute.GENERIC_LUCK)?.value ?: 0.0).toFloat())

        return lootTable.populateLoot(Random(), context.build())
    }

    fun spawnEntity(location: Location): Entity {
        val spawnedEntity = location.world.spawnEntity(location, EntityType.valueOf(config.type))
        injectEntity(spawnedEntity as LivingEntity)
        return spawnedEntity
    }

    fun injectEntity(entity: LivingEntity) {
        val state = EntityState(
            mobId = config.id,
            isMob = true,
            skills = config.skills.map { it.toState() }
        )

        globalManager.states.applyState(entity, state)
        entity.customName(config.name.toComponent())
        entity.isCustomNameVisible = true
        entity.removeWhenFarAway = config.flag.removeWhenFarAway
        entity.fireTicks = config.flag.fireTick
        entity.maximumNoDamageTicks = config.flag.noDamageTick
        entity.shieldBlockingDelay = config.flag.shieldBlockingDelay
        entity.isGlowing = config.flag.glowing

        config.attributes.forEach {
            if (entity.getAttribute(it.key) == null) {
                entity.registerAttribute(it.key)
            }

            entity.getAttribute(it.key)!!.baseValue = it.value
        }

        if (entity is Lootable) {
            entity.clearLootTable()
            if (config.loots.isNotEmpty()) {
                entity.lootTable = lootTable
            }
        }
    }

}