package com.ec.minecraft.enchantment

import com.ec.extension.GlobalManager
import com.ec.extension.enchantment.EnchantmentAPI
import com.ec.logger.Logger
import com.ec.model.Emoji
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Wither: EnchantmentAPI("wither") {

    override val emoji = Emoji.INFECTION
    override val display = "凋零"
    override val maxLevel = 1
    override val startLevel = 1
    override val description = listOf("")

    override fun isSupportedMaterial(): List<Material> {
        return listOf(
            Material.NETHERITE_SWORD,
            Material.DIAMOND_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.STONE_SWORD,
            Material.WOODEN_SWORD,
            Material.BOW,
            Material.CROSSBOW
        )
    }

    override fun initialize(globalManager: GlobalManager) {
        super.initialize(globalManager)

        globalManager.events {

            ProjectileHitEvent::class
                .observable(false, EventPriority.LOWEST)
                .filter {
                    it.hitEntity != null &&
                    it.entity.shooter is Player &&
                    globalManager.items.hasItemNBT((it.entity.shooter as Player).inventory.itemInMainHand)
                }
                .doOnError(Logger.trackError("Wither.ProjectileHitEvent", "error occurs in event subscriber"))
                .subscribe {
                    val attacker = it.entity.shooter as Player
                    val nbt = globalManager.items.deserializeFromItem(attacker.inventory.itemInMainHand)!!
                    if (nbt.enchantments.containsKey("wither")) {
                        (it.hitEntity as LivingEntity).addPotionEffect(PotionEffect(
                            PotionEffectType.WITHER,
                            20 * 5,
                            3,
                            true,
                            true
                        ))
                    }
                }

            EntityDamageByEntityEvent::class
                .observable(false, EventPriority.LOWEST)
                .filter {
                    it.damager is Player &&
                    (it.damager as Player).inventory.itemInMainHand.type != Material.AIR &&
                    globalManager.items.hasItemNBT((it.damager as Player).inventory.itemInMainHand)
                }
                .doOnError(Logger.trackError("Wither.EntityDamageByEntityEvent", "error occurs in event subscriber"))
                .subscribe {
                    val attacker = it.damager as Player
                    val nbt = globalManager.items.deserializeFromItem(attacker.inventory.itemInMainHand)!!
                    if (nbt.enchantments.containsKey("wither")) {
                        (it.entity as LivingEntity).addPotionEffect(PotionEffect(
                            PotionEffectType.WITHER,
                            20 * 5,
                            1,
                            true,
                            true
                        ))
                    }
                }

        }
    }

}