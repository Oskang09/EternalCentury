package com.ec.minecraft.command

import com.ec.manager.GlobalManager
import com.ec.model.player.ECPlayerGameState
import dev.reactant.reactant.extra.command.ReactantCommand
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import picocli.CommandLine

@CommandLine.Command(
    name = "back",
    description = ["回到上一次死亡的位置"]
)
internal class BackCommand(private val globalManager: GlobalManager): ReactantCommand() {

    private val lastDeathLocation= mutableMapOf<String, Location>()

    init {
        globalManager.events {
            PlayerDeathEvent::class
                .observable(false, EventPriority.HIGHEST)
                .filter { globalManager.players.getByPlayer(it.entity).gameState == ECPlayerGameState.FREE }
                .subscribe {
                    lastDeathLocation[it.entity.name] = it.entity.location
                }

            PlayerQuitEvent::class
                .observable(true, EventPriority.HIGHEST)
                .subscribe {
                    lastDeathLocation.remove(it.player.name)
                }
        }
    }

    override fun execute() {
        requireSenderIsPlayer()

        val player = sender as Player
        val ecPlayer = globalManager.players.getByPlayer(player)
        if (ecPlayer.gameState == ECPlayerGameState.ACTIVITY) {
            player.sendMessage(globalManager.message.system("您在活动状态无法进行传送！"))
            return
        }

        val lastLocation = lastDeathLocation.remove(player.name)
        if (lastLocation == null) {
            player.sendMessage(globalManager.message.system("并没有您上次死亡位置的记录！"))
            return
        }

        player.teleportAsync(lastLocation)
    }


}