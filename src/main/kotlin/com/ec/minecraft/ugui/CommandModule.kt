package com.ec.minecraft.ugui

import com.ec.manager.ugui.ModuleAPI
import me.oska.module.Module
import me.oska.module.ModuleNotConfigured
import me.oska.module.ModuleNotSupported
import me.oska.module.ModuleType
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CommandModule: ModuleAPI() {

    override fun getName(): String {
        return "CommandModule"
    }

    override fun getIdentifier(): String {
        return "command";
    }

    override fun supportParallel(): Boolean {
        return true;
    }

    override fun getModule(type: ModuleType, config: Map<*, *>): Module {
        if (type == ModuleType.REQUIREMENT) {
            throw ModuleNotSupported("command module doesn't support requirement option");
        }

        val commands = config["commands"] ?: throw ModuleNotConfigured("missing 'commands' from configuration.");
        val executeBy = config["executeBy"] ?: "SERVER"

        @Suppress("UNCHECKED_CAST")
        val cmd: List<String> = commands as? List<String> ?: throw ModuleNotConfigured("commands is not a string list, received $commands")
        val exec: String = executeBy as? String ?: throw ModuleNotConfigured("executeBy is not a string, received $executeBy")
        return ActionModule(exec, cmd);
    }

    internal class ActionModule constructor(
        private val executeBy: String,
        private val commands: List<String>,
    ): Module() {

        /*
            {
                "module": "command",
                "commands": [],
                "executeBy": "SERVER" | "PLAYER",
            }
        */

        private fun getCommands(player: Player): List<String> {
            return commands.map {
                command -> command.
                    replace("<player>", player.name)
            }
        }

        override fun check(player: Player): Boolean {
            return true;
        }

        override fun action(player: Player) = if (executeBy == "SERVER")
            getCommands(player).forEach {command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)}
        else
            getCommands(player).forEach {command -> player.performCommand(command)}

        override fun onFail(player: Player) {

        }

        override fun onSuccess(player: Player) {

        }
    }
}