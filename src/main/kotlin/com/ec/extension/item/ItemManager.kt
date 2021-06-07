package com.ec.extension.item

import com.ec.config.ItemConfig
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
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

@Component
class ItemManager(
    @Inject("plugins/server-data/items")
    private val itemConfigs: MultiConfigs<ItemConfig>
) {
    private val mapper = jacksonObjectMapper()
    private val items: MutableMap<String, ItemConfig> = mutableMapOf()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        itemConfigs.getAll(true).forEach {
            items[it.path] = it.content
        }
    }

    fun getItems(): MutableMap<String, ItemConfig> {
        return items
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

    fun getItemByKey(key: String): ItemStack {
        val data = items[key]!!
        val item = ItemStack(Material.valueOf(data.material))
        item.amount = data.amount

        val nbt = ItemNBT()
        item.itemMeta<ItemMeta> {
            val newLores = lore ?: mutableListOf()

            data.enchantments.forEach {
                val enchantment = globalManager.enchantments.getEnchantmentById(it.key)
                if (enchantment.isSupported(item.type)) {
                    nbt.enchantments[enchantment.id] = it.value
                    newLores.add(enchantment.getDisplayLore(it.value))

                    enchantment.getOrigin()?.let { ench ->
                        addEnchant(ench, it.value, true)
                    }
                }
            }

            setDisplayName(data.name.colorize())
            lore = newLores.colorize()
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        serializeToItem(item, nbt)
        return item
    }

}