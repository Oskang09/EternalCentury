package com.ec.extension.item

import com.ec.config.ItemConfig
import com.ec.config.RewardConfig
import com.ec.database.model.Item
import com.ec.database.model.Reward
import com.ec.model.ItemNBT
import com.ec.extension.GlobalManager
import com.ec.util.StringUtil.colorize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.tr7zw.nbtapi.NBTItem
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.core.dependency.injection.Inject
import dev.reactant.reactant.extensions.itemMeta
import dev.reactant.reactant.extra.config.type.MultiConfigs
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

@Component
class ItemManager(
    @Inject("plugins/EternalCentury/items")
    private val itemConfigs: MultiConfigs<ItemConfig>
) {
    private val mapper = jacksonObjectMapper()
    private val items: MutableMap<String, ItemConfig> = mutableMapOf()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        itemConfigs.getAll(true).forEach {
            items[it.content.id] = it.content
        }

        globalManager.events {

            PlayerInteractEvent::class
                .observable(false, EventPriority.LOWEST)
                .filter { it.action == Action.RIGHT_CLICK_AIR || it.action == Action.RIGHT_CLICK_BLOCK  }
                .filter { it.hasItem() && it.item != null && it.item!!.type != Material.AIR }
                .filter { hasItemNBT(it.item!!) }
                .subscribe {
                    val item = it.item!!
                    val nbt = deserializeFromItem(item)
                    if (nbt != null && nbt.id != "") {
                        val config = items[nbt.id]!!
                        if (config.consume) {
                            playerRemove(it.player, nbt.id, 1)
                            globalManager.sendRewardToPlayer(it.player, Reward(type = "command", commands = config.commands))
                        }
                    }
                }

        }
    }

    fun getRandomEggMaterial(): Material {
        return listOf(
            Material.BAT_SPAWN_EGG,
            Material.BEE_SPAWN_EGG,
            Material.BLAZE_SPAWN_EGG,
            Material.CAT_SPAWN_EGG,
            Material.CAVE_SPIDER_SPAWN_EGG,
            Material.CHICKEN_SPAWN_EGG,
            Material.COD_SPAWN_EGG,
            Material.COW_SPAWN_EGG,
            Material.CREEPER_SPAWN_EGG,
            Material.DOLPHIN_SPAWN_EGG,
            Material.DONKEY_SPAWN_EGG,
            Material.DROWNED_SPAWN_EGG,
            Material.ELDER_GUARDIAN_SPAWN_EGG,
            Material.ENDERMAN_SPAWN_EGG,
            Material.ENDERMITE_SPAWN_EGG,
            Material.EVOKER_SPAWN_EGG,
            Material.FOX_SPAWN_EGG,
            Material.GHAST_SPAWN_EGG,
            Material.GUARDIAN_SPAWN_EGG,
            Material.HOGLIN_SPAWN_EGG,
            Material.HORSE_SPAWN_EGG,
            Material.HUSK_SPAWN_EGG,
            Material.LLAMA_SPAWN_EGG,
            Material.MAGMA_CUBE_SPAWN_EGG,
            Material.MOOSHROOM_SPAWN_EGG,
            Material.MULE_SPAWN_EGG,
            Material.OCELOT_SPAWN_EGG,
            Material.PANDA_SPAWN_EGG,
            Material.PARROT_SPAWN_EGG,
            Material.PHANTOM_SPAWN_EGG,
            Material.PIG_SPAWN_EGG,
            Material.PIGLIN_SPAWN_EGG,
            Material.PIGLIN_BRUTE_SPAWN_EGG,
            Material.PILLAGER_SPAWN_EGG,
            Material.POLAR_BEAR_SPAWN_EGG,
            Material.PUFFERFISH_SPAWN_EGG,
            Material.RABBIT_SPAWN_EGG,
            Material.RAVAGER_SPAWN_EGG,
            Material.SALMON_SPAWN_EGG,
            Material.SHEEP_SPAWN_EGG,
            Material.SHULKER_SPAWN_EGG,
            Material.SILVERFISH_SPAWN_EGG,
            Material.SKELETON_SPAWN_EGG,
            Material.SKELETON_HORSE_SPAWN_EGG,
            Material.SLIME_SPAWN_EGG,
            Material.SPIDER_SPAWN_EGG,
            Material.SQUID_SPAWN_EGG,
            Material.STRAY_SPAWN_EGG,
            Material.STRIDER_SPAWN_EGG,
            Material.TRADER_LLAMA_SPAWN_EGG,
            Material.TROPICAL_FISH_SPAWN_EGG,
            Material.TURTLE_SPAWN_EGG,
            Material.VEX_SPAWN_EGG,
            Material.VILLAGER_SPAWN_EGG,
            Material.VINDICATOR_SPAWN_EGG,
            Material.WANDERING_TRADER_SPAWN_EGG,
            Material.WITCH_SPAWN_EGG,
            Material.WITHER_SKELETON_SPAWN_EGG,
            Material.WOLF_SPAWN_EGG,
            Material.ZOGLIN_SPAWN_EGG,
            Material.ZOMBIE_SPAWN_EGG,
            Material.ZOMBIE_HORSE_SPAWN_EGG,
            Material.ZOMBIE_VILLAGER_SPAWN_EGG,
            Material.ZOMBIFIED_PIGLIN_SPAWN_EGG
        ).random()
    }

    fun getItems(): MutableMap<String, ItemConfig> {
        return items
    }

    fun playerHas(player: Player, itemId: String, amount: Int = 1): Boolean {
        val item = globalManager.items.getItemByKey(itemId)
        item.amount = amount
        return player.inventory.contains(item)
    }

    fun playerRemove(player: Player, itemId: String, amount: Int = 1) {
        val item = globalManager.items.getItemByKey(itemId)
        item.amount = amount
        player.inventory.removeItem(item)
    }

    fun hasItemNBT(item: ItemStack): Boolean {
        return NBTItem(item).hasKey("ecnbt")
    }

    fun serializeToItem(item: ItemStack, nbt: ItemNBT) {
        val nbtItem = NBTItem(item)
        val json = mapper.writeValueAsString(nbt)
        nbtItem.setString("ecnbt", json)
        nbtItem.applyNBT(item)
    }

    fun deserializeFromItem(item: ItemStack): ItemNBT? {
        val nbtItem = NBTItem(item)
        if (nbtItem.hasKey("ecnbt")) {
            return mapper.readValue(nbtItem.getString("ecnbt"))
        }
        return null
    }

    fun getItem(data: Item): ItemStack {
        val item = ItemStack(Material.valueOf(data.material))
        item.amount = data.amount

        val nbt = ItemNBT()
        item.itemMeta<ItemMeta> {
            val newLores = lore ?: mutableListOf()

            newLores.addAll(data.lore)

            data.enchantments.forEach {
                val enchantment = globalManager.enchantments.getEnchantmentById(it.key)
                if (enchantment.isSupported(item.type)) {
                    nbt.enchantments[enchantment.id] = it.value
                    newLores.add(enchantment.getDisplayLore(it.value))

                    enchantment.origin?.let { ench ->
                        addEnchant(ench, it.value, true)
                    }
                }
            }

            if (data.name != "") {
                setDisplayName(data.name.colorize())
            }

            lore = newLores.colorize()
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        serializeToItem(item, nbt)
        return item
    }

    fun getItemByConfig(data: ItemConfig): ItemStack {
        val item = ItemStack(Material.valueOf(data.material))
        item.amount = data.amount

        val nbt = ItemNBT()
        item.itemMeta<ItemMeta> {
            val newLores = lore ?: mutableListOf()

            newLores.addAll(data.lore)

            data.enchantments.forEach {
                val enchantment = globalManager.enchantments.getEnchantmentById(it.key)
                if (enchantment.isSupported(item.type)) {
                    nbt.enchantments[enchantment.id] = it.value
                    newLores.add(enchantment.getDisplayLore(it.value))

                    enchantment.origin?.let { ench ->
                        addEnchant(ench, it.value, true)
                    }
                }
            }

            if (data.name != "") {
                setDisplayName(data.name.colorize())
            }

            lore = newLores.colorize()
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        nbt.id = data.id
        serializeToItem(item, nbt)
        return item
    }

    fun getItemByKey(key: String): ItemStack {
        return getItemByConfig(items[key]!!)
    }

}