package com.ec.manager.enchantment

import com.ec.database.Players
import com.ec.model.ItemNBT
import com.ec.manager.GlobalManager
import com.ec.logger.Logger
import com.ec.util.RandomUtil
import com.ec.util.StringUtil.toComponent
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import kotlin.random.Random

@Component
class EnchantmentManager {
    private val enchantments: MutableMap<String, EnchantmentAPI> = HashMap();
    private val originEnchantments: MutableMap<Enchantment, EnchantmentAPI> = HashMap();
    private lateinit var globalManager: GlobalManager
    private val mapper = jacksonObjectMapper()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.reflections.loopEnchantments {
            it.initialize(globalManager)
            val origin = it.origin
            if (origin != null) {
                originEnchantments[origin] = it
            }
            enchantments[it.id] = it
        }

        globalManager.events {

            PrepareAnvilEvent::class
                .observable(true, EventPriority.LOWEST)
                .doOnError(Logger.trackError("EnchantmentManager.PrepareAnvilEvent", "error occurs in event subscriber"))
                .subscribe {
                    val player = it.view.player as Player
                    val rename = it.inventory.renameText
                    val itemLeft = it.inventory.getItem(0)
                    val itemRight = it.inventory.getItem(1)

                    Logger.withTrackerPlayerEvent(player, it, "EnchantManager.PrepareAnvilEvent", "player ${player.uniqueId} error occurs when enchant") {
                        if (it.view.title().contains("&f[&5系统&f]".toComponent())) {
                            it.result = globalManager.component.item(Material.PLAYER_HEAD) { meta ->
                                meta.displayName("&b[&5系统&b] &a确认 &f- &f${it.inventory.renameText}".toComponent())
                            }
                            return@withTrackerPlayerEvent
                        }

                        val ecPlayer = globalManager.players.getByPlayer(player)
                        if (itemLeft != null && itemRight != null && it.result == null) {
                            if (itemRight.type == Material.ENCHANTED_BOOK || itemRight.type == itemLeft.type) {
                                if (originEnchantments[Enchantment.DURABILITY]!!.isSupported(itemLeft.type)) {
                                    it.result = ItemStack(itemLeft.type)
                                }
                            }
                        }

                        var result = it.result ?: return@withTrackerPlayerEvent
                        it.inventory.repairCost = if (itemLeft?.type == itemRight?.type) 10 else 0
                        if (rename != null) {
                            it.inventory.repairCost += 1
                            if (rename.contains("&")) {
                                it.inventory.repairCost += 1
                                result.itemMeta<ItemMeta> {
                                    displayName(rename.toComponent())
                                }
                            }
                        }

                        if (itemLeft != null && itemRight != null) {
                            var totalLevelUpgraded = 0
                            var enchantments = mutableMapOf<EnchantmentAPI, Int>()

                            val leftNbt = globalManager.items.deserializeFromItem(itemLeft)
                            totalLevelUpgraded += if (leftNbt != null) {
                                mergeEnchantments(enchantments, leftNbt.enchantments)
                            } else {
                                mergeEnchantments(enchantments, if (itemLeft.type == Material.ENCHANTED_BOOK) {
                                    (itemLeft.itemMeta as EnchantmentStorageMeta).storedEnchants
                                } else {
                                    itemLeft.enchantments
                                })
                            }

                            val rightNbt = globalManager.items.deserializeFromItem(itemRight)
                            totalLevelUpgraded += if (rightNbt != null) {
                                mergeEnchantments(enchantments, rightNbt.enchantments)
                            } else {
                                mergeEnchantments(enchantments, if (itemRight.type == Material.ENCHANTED_BOOK) {
                                    (itemRight.itemMeta as EnchantmentStorageMeta).storedEnchants
                                } else {
                                    itemRight.enchantments
                                })
                            }

                            if (enchantments.size > 5) {
                                enchantments = enchantments.filter { ench ->  ench.key.isSupported(result.type) }.toMutableMap()
                                while (enchantments.size > 5) {
                                    val randomKey = enchantments.keys.random(Random(ecPlayer.database[Players.enchantmentRandomSeed]))
                                    totalLevelUpgraded -= enchantments.remove(randomKey)!!
                                }
                            }

                            val itemNbt = ItemNBT()
                            result.itemMeta<ItemMeta> {

                                enchants.forEach { ench ->
                                    removeEnchant(ench.key)
                                }

                                val newLores = mutableListOf<net.kyori.adventure.text.Component>()
                                enchantments.forEach { (ench, level) ->
                                    if (!ench.isSupported(result.type)) {
                                        totalLevelUpgraded -= level
                                        return@forEach
                                    }

                                    itemNbt.enchantments[ench.id] = level
                                    newLores.add(ench.getDisplayLore(level))
                                    ench.origin?.let { origin ->
                                        addEnchant(origin, level, true)
                                    }
                                }

                                lore(newLores)
                                addItemFlags(ItemFlag.HIDE_ENCHANTS)
                                if (result.type == Material.ENCHANTED_BOOK) {
                                    addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
                                }
                            }

                            globalManager.items.serializeToItem(it.result!!, itemNbt)
                            it.inventory.repairCost += totalLevelUpgraded
                            if (it.inventory.repairCost < 0) {
                                it.inventory.repairCost = 0
                            }
                        }
                    }
                }

            EnchantItemEvent::class
                .observable(true, EventPriority.LOWEST)
                .doOnError(Logger.trackError("EnchantmentManager.EnchantItemEvent", "error occurs in event subscriber"))
                .subscribe {
                    val player = it.enchanter
                    Logger.withTrackerPlayerEvent(player, it, "EnchantManager - EnchantItemEvent", "player ${player.uniqueId} error occurs when enchant") {

                        val itemNbt = ItemNBT()
                        it.item.itemMeta<ItemMeta> {
                            val newLores = mutableListOf<net.kyori.adventure.text.Component>()

                            it.enchantsToAdd.forEach { enchant ->
                                val ench = getEnchantmentByOrigin(enchant.key)

                                itemNbt.enchantments[ench.id] = enchant.value
                                newLores.add(ench.getDisplayLore(enchant.value))
                            }

                            lore(newLores)
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            if (it.item.type == Material.BOOK) {
                                addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
                            }
                        }

                        globalManager.items.serializeToItem(it.item, itemNbt)
                    }
            }
        }
    }

    fun getEnchantmentByOrigin(origin: Enchantment): EnchantmentAPI {
        return originEnchantments[origin]!!
    }

    fun getEnchantmentById(id: String): EnchantmentAPI {
        return enchantments[id]!!
    }

    fun getEnchantments(): MutableCollection<EnchantmentAPI> {
        return enchantments.values
    }

    fun getRandomEnchantedBook(numOfEnchantments: Int, levelCapped: Int): ItemStack {
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val itemNbt = ItemNBT()
        val itemEnchantments = mutableMapOf<String, Int>()
        item.itemMeta<EnchantmentStorageMeta> {
            val newLores = lore() ?: mutableListOf()

            repeat(numOfEnchantments) {
                val randomKey = enchantments.keys.random()
                val ench = enchantments[randomKey]!!
                var level = RandomUtil.randomInteger(ench.maxLevel) + 1
                if (level > levelCapped) {
                   level = levelCapped
                    if (level > ench.maxLevel) {
                        level = ench.maxLevel
                    }
                }
                itemEnchantments[randomKey] = level
            }

            itemEnchantments.forEach { (enchantment, level) ->
                val ench = getEnchantmentById(enchantment)
                newLores.add(ench.getDisplayLore(level))
                ench.origin?.let { origin ->
                    addStoredEnchant(origin, level, true)
                }

                itemNbt.enchantments[enchantment] = level
            }

            lore(newLores)
            addItemFlags(*ItemFlag.values())
        }

        globalManager.items.serializeToItem(item, itemNbt)
        return item
    }

    fun getEnchantedBookByMap(enchantments: Map<String, Int>): ItemStack {
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val itemNbt = ItemNBT()
        item.itemMeta<EnchantmentStorageMeta> {
            val newLores = lore() ?: mutableListOf()
            enchantments.forEach { (enchantment, level) ->
                val ench = getEnchantmentById(enchantment)
                newLores.add(ench.getDisplayLore(level))
                ench.origin?.let { origin ->
                    addStoredEnchant(origin, level, true)
                }

                itemNbt.enchantments[enchantment] = level
            }

            lore(newLores)
            addItemFlags(*ItemFlag.values())
        }

        globalManager.items.serializeToItem(item, itemNbt)
        return item
    }

    @JvmName("mergeSerializedEnchantments")
    fun mergeEnchantments(sourceMap: MutableMap<EnchantmentAPI, Int>, incomingMap: MutableMap<String, Int>): Int {
        var upgradedLevel: Int = 0
        incomingMap.forEach {
            val enchantment = getEnchantmentById(it.key)
            val source = sourceMap[enchantment] ?: 0
            val current = it.value
            val combine = source + current
            val max = enchantment.maxLevel
            if (combine > max) {
                sourceMap[enchantment] = max
            } else {
                sourceMap[enchantment] = combine
            }
            upgradedLevel += (sourceMap[enchantment]!! - source)
        }
        return upgradedLevel
    }

    fun mergeEnchantments(sourceMap: MutableMap<EnchantmentAPI, Int>, incomingMap: MutableMap<EnchantmentAPI, Int>): Int {
        var upgradedLevel: Int = 0
        incomingMap.forEach {
            val source = sourceMap[it.key] ?: 0
            val current = it.value
            val combine = source + current
            val max = it.key.maxLevel
            if (combine > max) {
                sourceMap[it.key] = max
            } else {
                sourceMap[it.key] = combine
            }
            upgradedLevel += (sourceMap[it.key]!! - source)
        }
        return upgradedLevel
    }

    @JvmName("mergeOriginEnchantments")
    fun mergeEnchantments(sourceMap: MutableMap<EnchantmentAPI, Int>, incomingMap: MutableMap<Enchantment, Int>): Int {
        var upgradedLevel: Int = 0
        incomingMap.forEach {
            val originEnchantment = getEnchantmentByOrigin(it.key)
            val source = sourceMap[originEnchantment] ?: 0
            val current = it.value
            val combine = source + current
            val max = originEnchantment.maxLevel
            if (combine > max) {
                sourceMap[originEnchantment] = max
            } else {
                sourceMap[originEnchantment] = combine
            }
            upgradedLevel += (sourceMap[originEnchantment]!! - source)
        }
        return upgradedLevel
    }

}
