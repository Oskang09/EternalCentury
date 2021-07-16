package com.ec.manager.arena

import com.ec.config.arena.ArenaConfig
import com.ec.config.arena.ArenaRewardConfigType
import com.ec.manager.GlobalManager
import com.ec.manager.mob.IEntity
import com.ec.model.app.Reward
import com.ec.util.ChanceUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.pow

class MobArena(globalManager: GlobalManager, config: ArenaConfig, host: Player, spawn: Location, lobby: Location) : IArena(globalManager, config, host, spawn, lobby) {

    private var wave: Int = 0
    private val bossEntities = hashSetOf<Int>()
    private var droppedExp: Int = 0
    private val drops = mutableListOf<ItemStack>()

    override fun ready() {
        globalManager.events {
            disposer.add(
                EntityDeathEvent::class
                    .observable(false, EventPriority.HIGHEST)
                    .filter { it.entity.world.name == "dungeon" }
                    .filter { entities.contains(it.entity.entityId) }
                    .subscribe {
                        drops.addAll(it.drops)
                        it.drops.clear()

                        droppedExp += it.droppedExp
                        it.droppedExp = 0
                    }
            )
        }
    }

    override fun start() {
        val arenaSetting = config.mobArena!!
        val waveSetting = arenaSetting.waves[wave]

        waveSetting.mobs
            .map {
                val entity = mutableListOf<IEntity>()
                repeat(it.count) { _ ->  entity.add(globalManager.mobs.getMobById(it.mob)) }
                return@map entity
            }
            .plus(
                waveSetting.extra
                    .map {
                        val entity = mutableListOf<IEntity>()
                        repeat(it.count) { _ ->
                            if (ChanceUtil.defaultChance(it.chance)) {
                                entity.add(globalManager.mobs.getMobById(it.mob))
                            }
                        }
                        return@map entity
                    }
            )
            .flatten()
            .forEach {
                globalManager.runInMainThread {
                    val spawned = it.spawnEntity(arenaSetting.locations.random().location)
                    entities.add(spawned.entityId)
                    globalManager.arenas.showEntityToPlayers(players, spawned)
                }
            }

        globalManager.states.delayedTask(waveSetting.nextWave) {
            if (wave + 1 < arenaSetting.waves.size) {
                wave += 1
                start()
                return@delayedTask
            }

            arenaSetting.bossWave.mobs
                .map {
                    val entity = mutableListOf<IEntity>()
                    repeat(it.count) { _ ->  entity.add(globalManager.mobs.getMobById(it.mob)) }
                    return@map entity
                }
                .plus(
                    arenaSetting.bossWave.extra
                        .map {
                            val entity = mutableListOf<IEntity>()
                            repeat(it.count) { _ ->
                                if (ChanceUtil.defaultChance(it.chance)) {
                                    entity.add(globalManager.mobs.getMobById(it.mob))
                                }
                            }
                            return@map entity
                        }
                )
                .flatten()
                .forEach {
                    globalManager.runInMainThread {
                        val spawned = it.spawnEntity(arenaSetting.locations.random().location)
                        entities.add(spawned.entityId)
                        bossEntities.add(spawned.entityId)
                        globalManager.arenas.showEntityToPlayers(players, spawned)
                    }
                }

            globalManager.events {
                disposer.add(
                    EntityDeathEvent::class
                        .observable(false, EventPriority.MONITOR)
                        .filter { it.entity.world.name == "dungeon" }
                        .filter { bossEntities.remove(it.entity.entityId) }
                        .subscribe {
                            if (bossEntities.isEmpty()) {
                                onEnd()
                            }
                        }
                )
            }
        }
    }

    override fun end() {
        /**
         * (allExp / numOfPlayers) ^ ( 1.0 + (numOfPlayers / 10 ))
         * Example 3 players = 30 / 5 ^ 1.0 + 0.3 = 19
         */
        val onlinePlayers = players.filter { it.isOnline }
        val numOfPlayers = onlinePlayers.size.toDouble()
        val averageExp = (droppedExp / numOfPlayers).pow(1.0 + (numOfPlayers / 10))

        val mappedDrops = mutableMapOf<Player, MutableList<ItemStack>>()
        val mappedRewards = mutableMapOf<Player, MutableList<Reward>>()
        while (drops.isNotEmpty()) {
            val target = onlinePlayers.random()
            val playersItem = mappedDrops.getOrPut(target) { mutableListOf() }
            if (playersItem.size >= drops.size / numOfPlayers) {
                continue
            }

            playersItem.add(drops.random())
            mappedDrops[target] = playersItem
        }

        config.rewards.forEach {
            when (it.type) {
                ArenaRewardConfigType.EQUAL -> {
                    onlinePlayers.forEach { target ->
                        val playersReward = mappedRewards.getOrPut(target) { mutableListOf()}
                        playersReward.add(it.reward)
                        mappedRewards[target] = playersReward
                    }
                }
                ArenaRewardConfigType.RANDOM -> {
                    val target = onlinePlayers.random()
                    val playersReward = mappedRewards.getOrPut(target) { mutableListOf()}
                    playersReward.add(it.reward)
                    mappedRewards[target] = playersReward
                }
            }
        }

        mappedRewards.forEach { (p, u) -> globalManager.sendRewardToPlayer(p, u)  }
        mappedDrops.forEach { (p, i) -> globalManager.givePlayerItem(p.name, i) }
        onlinePlayers.forEach { it.giveExp(averageExp.toInt()) }
    }
}