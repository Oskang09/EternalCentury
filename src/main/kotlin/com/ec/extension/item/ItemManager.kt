package com.ec.extension.item

import com.ec.config.ItemData
import com.ec.extension.GlobalManager
import com.ec.util.StringUtil.colorize
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
    private val itemConfigs: MultiConfigs<ItemData>
) {

    private val items: MutableMap<String, ItemData> = mutableMapOf()
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        itemConfigs.getAll(true).forEach {
            items[it.path] = it.content
        }
    }

    fun getItems(): MutableMap<String, ItemData> {
        return items
    }

    fun getItemByKey(key: String): ItemStack {
        val data = items[key]!!
        val item = ItemStack(Material.valueOf(data.material));
        item.amount = data.amount

        val nbtEnch = mutableMapOf<String, Int>()
        item.itemMeta<ItemMeta> {
            val newLores = lore ?: mutableListOf()

            data.enchantments.forEach {
                val enchantment = globalManager.enchantments.getEnchantmentByName(it.key)

                nbtEnch[it.key] = it.value
                newLores.add(enchantment.getDisplayLore(item.type, it.value))

                enchantment.getOrigin()?.let { ench ->
                    addEnchant(ench, it.value, true)
                }
            }

            setDisplayName(data.name.colorize())
            lore = newLores.colorize()
            addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        val nbt = NBTItem(item)
        nbt.setObject("ec_ench", nbtEnch)
        return nbt.item
    }

}