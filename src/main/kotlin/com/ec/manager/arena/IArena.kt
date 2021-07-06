package com.ec.manager.arena

import com.ec.config.arena.ArenaConfig
import com.ec.config.arena.MobArenaWaveConfig
import com.ec.manager.GlobalManager
import com.ec.manager.mob.IEntity
import com.ec.minecraft.inventory.filter.YesOrNoUI
import com.ec.model.player.ECPlayerGameState
import com.ec.util.ChanceUtil
import com.ec.util.StringUtil.toColorized
import com.gmail.nossr50.datatypes.party.Party
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class IArena(
    val globalManager: GlobalManager,
    val config: ArenaConfig,
    var host: Player,
    private val spawn: Location,
    private val lobby: Location,
) {

    private val bossEntities = hashSetOf<Int>()
    private val disposer = mutableListOf<Disposable>()

    val id = config.id + "@" + UUID.randomUUID().toString()
    var wave: Int = 0
    val entities = hashSetOf<Int>()
    val players = hashSetOf(host)
    var isStarted: Boolean = false

    private val queue: Queue<Player> = ArrayDeque()
    private var queueTask: String = ""
    private var isRequest: Boolean = false

    fun onJoin(player: Player) {
        if (config.cooldown != null) {
            val lastAt = globalManager.states.getPlayerState(player).cooldown[config.cooldown.id]
            if (lastAt != null && ChronoUnit.SECONDS.between(Instant.ofEpochSecond(lastAt), Instant.now()) < config.cooldown.second) {
                player.sendMessage(globalManager.message.system("您的副本还在冷却中。"))
                return
            }
        }

        if (isStarted) {
            player.sendMessage(globalManager.message.system("副本已经开始了，下次请早。"))
            return
        }

        if (players.size >= config.limit) {
            player.sendMessage(globalManager.message.system("副本已经满人了，下次请早。"))
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
                    player,
                    YesOrNoUI.YesOrNoUIProps(
                        title = "&f玩家 ${requester.name} 要加入您的副本".toColorized(),
                        onYes = onYes@{
                            isRequest = false

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
        players.parallelStream().forEach {
            it.teleportAsync(lobby)
            entities.add(it.entityId)
            it.sendMessage(globalManager.message.system("副本在15秒后开始，请做好准备！"))
            if (config.cooldown != null) {
                globalManager.states.updatePlayerState(it) { state ->
                    state.cooldown[config.cooldown.id] = Instant.now().epochSecond
                }
            }
        }

        globalManager.states.delayedTask(15) {
            players.parallelStream().forEach { it.teleportAsync(config.spawn.random().location) }

            when (config.type) {
                "mobarena" -> globalManager.runOffMainThread { mobarenaProcessWave() }
            }
        }
    }

    fun onEnd() {
        disposer.forEach { it.dispose() }

        config.rewards.forEach { globalManager.sendRewardToPlayer(players.random(), it) }
        players.parallelStream().forEach {
            val ecPlayer = globalManager.players.getByPlayer(it)
            ecPlayer.gameState = ECPlayerGameState.FREE
            ecPlayer.gameName = ""
            it.teleportAsync(spawn)
        }
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
                    entities.add(it.spawnEntity(arenaSetting.locations.random().location).entityId)
                }
            }

        globalManager.states.delayedTask(waveSetting.nextWave) {
            if (wave < arenaSetting.waves.size) {
                wave += 1
                mobarenaProcessWave()
                return@delayedTask
            }

            globalManager.events {
                disposer.add(
                    EntityDeathEvent::class
                    .observable(false, EventPriority.MONITOR)
                    .filter { bossEntities.remove(it.entity.entityId) }
                    .subscribe {
                        if (bossEntities.isEmpty()) {
                            onEnd()
                        }
                    }
                )
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
                        entities.addAll(bossEntities)
                        bossEntities.add(it.spawnEntity(arenaSetting.locations.random().location).entityId)
                    }
                }
        }
    }


}