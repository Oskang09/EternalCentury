package com.ec.minecraft.enchantment.skills

import com.ec.config.SkillConfig
import com.ec.manager.enchantment.EnchantmentAPI
import com.ec.model.Emoji
import org.bukkit.Material

class LiXingHuo: EnchantmentAPI("lixinghuo") {

    override val description = listOf("")
    override val emoji = Emoji.FIRE
    override val display = "离星火"
    override val maxLevel = 5
    override val startLevel = 1

    override val skills = listOf(
        SkillConfig("LiXingHuo", 1)
    )

    override fun isSupportedMaterial(): List<Material> {
        return listOf(
            Material.LEATHER_CHESTPLATE,
            Material.CHAINMAIL_CHESTPLATE,
            Material.IRON_CHESTPLATE,
            Material.DIAMOND_CHESTPLATE,
            Material.GOLDEN_CHESTPLATE,
            Material.NETHERITE_CHESTPLATE,
        )
    }

}