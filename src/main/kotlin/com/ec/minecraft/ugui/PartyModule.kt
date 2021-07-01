package com.ec.minecraft.ugui

import com.ec.manager.GlobalManager
import com.ec.manager.ugui.ModuleAPI
import com.ec.model.player.ECPlayerGameState
import me.darkeyedragon.randomtp.RandomTeleport
import me.darkeyedragon.randomtp.SpigotImpl
import me.darkeyedragon.randomtp.api.teleport.TeleportParticle
import me.darkeyedragon.randomtp.common.world.location.search.LocationSearcherFactory
import me.darkeyedragon.randomtp.teleport.Teleport
import me.darkeyedragon.randomtp.teleport.TeleportProperty
import me.darkeyedragon.randomtp.util.WorldUtil
import me.oska.module.Module
import me.oska.module.ModuleType
import net.skinsrestorer.shade.paperlib.PaperLib
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import java.util.*

class PartyModule: ModuleAPI() {

    override fun getIdentifier(): String {
        return "party"
    }

    override fun getName(): String {
        return "PartyModule"
    }

    override fun supportParallel(): Boolean {
        return true
    }

    override fun getModule(type: ModuleType, config: Map<*, *>): Module {
        return ActionModule(
            type, globalManager,
            config, Bukkit.getPluginManager().getPlugin("RandomTeleport") as SpigotImpl
        )
    }

    internal class ActionModule(
        private val type: ModuleType,
        private val globalManager: GlobalManager,
        private val config: Map<*, *>,
        private val rtp: SpigotImpl,
    ): Module() {

        /*
            {
                "module": "party",
                "challenge": "",
                "num_of_member": 1,
                "type": "COMMAND" | "TELEPORT" | "rtp",
                "activity": "",
                "world": "nether", // rtp
                "teleport_id": "", // TELEPORT
                "commands": [], // COMMAND
            }
        */

        private val challenge  = config["challenge"] as String

        private fun searchAndTeleport(players: List<Player>, world: String, rtp: RandomTeleport) {
            val randomWorld = WorldUtil.toRandomWorld(Bukkit.getWorld(world))
            val randomLocation = rtp.worldQueue.popLocation(randomWorld)
            val location = WorldUtil.toLocation(randomLocation)
            PaperLib.getChunkAtAsync(location).thenAccept {
                val searcher = LocationSearcherFactory.getLocationSearcher(randomWorld, rtp)
                if (!searcher.isSafe(randomLocation)) {
                    searchAndTeleport(players, world, rtp)
                    return@thenAccept
                }

                val block = it.world.getBlockAt(location)
                val targetLocation = block.location.add(0.5, 1.5, 0.5)
                players.forEach { p -> p.teleportAsync(targetLocation) }
                globalManager.states.delayedTask(1) {
                    val worldConfig = rtp.locationFactory.getWorldConfigSection(randomWorld)
                    rtp.worldQueue.get(randomWorld).generate(worldConfig, 1)
                }
            }
        }

        override fun action(player: Player) {
            when (type) {
                ModuleType.REQUIREMENT -> {}
                ModuleType.ITEM_PROVIDER -> {}
                ModuleType.REWARD -> {
                    val activity = config["activity"] as String
                    globalManager.mcmmo.getPlayerParty(player).forEach { p ->
                        globalManager.players.getByPlayer(p).activityName = activity
                        globalManager.players.getByPlayer(p).gameState = if (activity == "") {
                            ECPlayerGameState.FREE
                        } else {
                            ECPlayerGameState.ACTIVITY
                        }
                    }

                    when ((config["type"] as String).lowercase()) {
                        "rtp" ->  {
                            val world = config["world"] as String
                            searchAndTeleport(globalManager.mcmmo.getPlayerParty(player), world, rtp.instance)
                        }
                        "teleport" ->  {
                            val teleportId = config["teleport_id"] as String
                            globalManager.mcmmo.partyTeleport(player, challenge, teleportId)
                        }
                        "command" -> {
                            val commands = config["commands"] as List<String>
                            globalManager.mcmmo.partyCommands(player, commands)
                        }
                    }
                }
            }
        }

        override fun check(player: Player): Boolean {
            if (type == ModuleType.REQUIREMENT) {
                val numOfMembers = config["num_of_member"] as Int? ?: 0
                if (numOfMembers > 0) {
                    val partyMembers = globalManager.mcmmo.getPlayerParty(player)
                    if (partyMembers.size != numOfMembers) {
                        return false
                    }
                }
                return globalManager.mcmmo.partyIsNearby(player, challenge)
            }
            return true
        }

        override fun onFail(player: Player) {

        }

        override fun onSuccess(player: Player) {

        }

    }

}