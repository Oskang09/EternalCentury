package com.ec.manager.arena

import com.ec.config.arena.ArenaConfig
import com.ec.manager.GlobalManager
import com.ec.manager.mob.IEntity
import com.ec.minecraft.inventory.filter.YesOrNoUI
import com.ec.model.player.ECPlayerGameState
import com.ec.util.ChanceUtil
import com.ec.util.StringUtil.toColorized
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.inventory.ItemStack
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.pow

class IArena(
    val globalManager: GlobalManager,
    val config: ArenaConfig,
    var host: Player,
    private val spawn: Location,
    private val lobby: Location,
) {

    val id = config.id + "@" + UUID.randomUUID().toString()
    var wave: Int = 0
    val players = hashSetOf(host)
    var isStarted: Boolean = false
    val entities = hashSetOf<Int>()

    private val bossEntities = hashSetOf<Int>()
    private val disposer = mutableListOf<Disposable>()
    private var droppedExp: Int = 0
    private val drops = mutableListOf<ItemStack>()
    private val queue: Queue<Player> = ArrayDeque()
    private var queueTask: String = ""
    private var isRequest: Boolean = false

    init {
        val ecPlayer = globalManager.players.getByPlayer(host)
        ecPlayer.gameState = ECPlayerGameState.ARENA
        ecPlayer.gameName = id
    }

    fun onJoin(player: Player) {
        if (config.cooldown != null) {
            val lastAt = globalManager.states.getPlayerState(player).cooldown[config.cooldown.id]
            if (lastAt != null && ChronoUnit.SECONDS.between(Instant.ofEpochSecond(lastAt), Instant.now()) < config.cooldown.second) {
                player.sendMessage(globalManager.message.system("您的副本还在冷却中。"))
                return
            }
        }

        if (players.size >= config.limit) {
            player.sendMessage(globalManager.message.system("副本已经满人了，下次请早。"))
            return
        }

        if (isStarted) {
            player.sendMessage(globalManager.message.system("副本已经开始了，下次请早。"))
            return
        }

        if (globalManager.players.getByPlayer(player).gameState != ECPlayerGameState.FREE) {
            player.sendMessage(globalManager.message.system("您已经在副本或活动中了，无法加入其他副本。"))
            return
        }

        player.sendMessage(globalManager.message.system("等待房主答应您的请求。"))
        queue.add(player)
        queueTask = globalManager.states.asyncContinuousTask(1) {
            if (queue.isEmpty()) {
                globalManager.states.disposeTask(queueTask)
                return@asyncContinuousTask
            }

            if (!isRequest) {
                isRequest = true

                val requester = queue.remove()
                globalManager.inventory.displaySelection(
                    host,
                    YesOrNoUI.YesOrNoUIProps(
                        title = "&f玩家 ${requester.name} 要加入您的副本".toColorized(),
                        onYes = onYes@{
                            isRequest = false
                            host.closeInventory()

                            if (!requester.isOnline) {
                                host.sendMessage("玩家 ${requester.name} 已经离线了。")
                                return@onYes
                            }

                            val ecPlayer = globalManager.players.getByPlayer(requester)
                            if (ecPlayer.gameState != ECPlayerGameState.FREE) {
                                host.sendMessage("玩家 ${requester.name} 已经在其他副本了。")
                                return@onYes
                            }

                            players.add(requester)
                            ecPlayer.gameState = ECPlayerGameState.ARENA
                            ecPlayer.gameName = id

                            players.forEach {
                                it.sendMessage(globalManager.message.system("玩家 ${requester.name} 加入了副本。"))
                            }

                            if (players.size >= config.limit) {
                                queue.forEach {
                                    it.sendMessage(globalManager.message.system("副本已经满人了，下次请早。"))
                                }
                                queue.clear()
                            }
                        },
                        onNo = {
                            isRequest = false
                            host.closeInventory()

                            requester.sendMessage(globalManager.message.system("房主拒绝了您的加入请求。"))
                        }
                    )
                )
            }
        }
    }

    fun onQuit(player: Player): Boolean {
        if (isStarted) {
            player.sendMessage(globalManager.message.system("副本已经开始了是无法退出。"))
            return false
        }

        val ecPlayer = globalManager.players.getByPlayer(player)
        players.forEach {it.sendMessage(globalManager.message.system("玩家 ${it.name} 离开了副本组队。")) }
        players.remove(player)
        ecPlayer.gameState = ECPlayerGameState.FREE
        ecPlayer.gameName = ""
        if (player.name == host.name) {
            if (players.size == 0) {
                globalManager.arenas.removeArenaById(id)
                return true
            }

            host = players.random()
            players.forEach { it.sendMessage(globalManager.message.system("由于房主退出了，随机选择了 ${host.name} 为房主。")) }
        }
        return true
    }

    fun onStart() {
        if (players.size > config.limit) {
            host.sendMessage(globalManager.message.system("您的副本组队中已经超过可加入的人数了。"))
            return
        }

        isStarted = true
        players.forEach {
            it.teleportAsync(lobby)
            entities.add(it.entityId)
            globalManager.arenas.showEntityToPlayers(players, it)
            it.sendMessage(globalManager.message.system("副本在15秒后开始，请做好准备！"))
            if (config.cooldown != null) {
                globalManager.states.updatePlayerState(it) { state ->
                    state.cooldown[config.cooldown.id] = Instant.now().epochSecond
                }
            }
        }

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

        globalManager.states.delayedTask(15) {
            players.forEach { it.teleportAsync(config.spawn.random().location) }

            when (config.type) {
                "mobarena" -> mobarenaProcessWave()
            }
        }
    }

    fun onDisconnect(player: Player) {
        player.teleportAsync(lobby)
    }

    fun onReconnect(player: Player) {
        player.teleportAsync(config.spawn.random().location)
    }

    fun onEnd() {
        disposer.forEach { it.dispose() }

        /**
         * (allExp / numOfPlayers) ^ ( 1.0 + (numOfPlayers / 10 ))
         * Example 3 players = 30 / 5 ^ 1.0 + 0.3 = 19
         */
        val onlinePlayers = players.filter { it.isOnline }
        val numOfPlayers = onlinePlayers.size.toDouble()
        val averageExp = (droppedExp / numOfPlayers).pow(1.0 + (numOfPlayers / 10))

        val mappedDrops = mutableMapOf<Player, MutableList<ItemStack>>()
        drops.forEach {
            mappedDrops.getOrPut(onlinePlayers.random()) { mutableListOf() }.add(it)
        }

        config.rewards.forEach { globalManager.sendRewardToPlayer(onlinePlayers.random(), it) }
        mappedDrops.forEach { (p, i) -> globalManager.givePlayerItem(p.name, i) }
        onlinePlayers.forEach { it.giveExp(averageExp.toInt()) }

        globalManager.runInMainThread {
            onlinePlayers.forEach {
                val ecPlayer = globalManager.players.getByPlayer(it)
                ecPlayer.gameState = ECPlayerGameState.FREE
                ecPlayer.gameName = ""
                it.teleportAsync(spawn)
            }
        }

        globalManager.arenas.removeArenaById(id)
    }

    private fun mobarenaProcessWave() {
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
                mobarenaProcessWave()
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


}