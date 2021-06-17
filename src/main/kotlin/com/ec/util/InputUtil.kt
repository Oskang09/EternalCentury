package com.ec.util

import com.ec.ECCore
import com.ec.util.StringUtil.colorize
import dev.reactant.reactant.extensions.itemMeta
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

object InputUtil {

    private fun extractValueFromText(text: String): String {
        return text.replace("&b[&5系统&b] &a确认 &f- &f".colorize(), "")
    }

    private fun getRequestItem() : ItemStack {
        val item = ItemStack(Material.PLAYER_HEAD)
        item.itemMeta<ItemMeta> {
            setDisplayName("")
        }
        return item
    }

    fun requestString(
        opener: Player,
        title: String,
        onCancel: (Player) -> Unit,
        onComplete: (Player, String) -> AnvilGUI.Response,
    ) {
        AnvilGUI.Builder()
            .onComplete { player, text -> onComplete(player, extractValueFromText(text)) }
            .onClose(onCancel)
            .text("")
            .itemLeft(getRequestItem())
            .title("&f[&5系统&f] &0${title}".colorize())
            .plugin(ECCore.instance)
            .open(opener)
    }

    fun requestInteger(
        opener: Player,
        title: String,
        onCancel: (Player) -> Unit,
        onComplete: (Player, Int) -> AnvilGUI.Response,
    ) {
        AnvilGUI.Builder()
            .onComplete { player, text ->
                val input = extractValueFromText(text)
                val value = input.toIntOrNull() ?: return@onComplete AnvilGUI.Response.text("&f输入错误，您必须输入数字.")
                onComplete(player, value)
            }
            .onClose(onCancel)
            .text("")
            .itemLeft(getRequestItem())
            .title("&f[&5系统&f] &0${title}".colorize())
            .plugin(ECCore.instance)
            .open(opener)
    }

    fun requestDouble(
        opener: Player,
        title: String,
        onCancel: (Player) -> Unit,
        onComplete: (Player, Double) -> AnvilGUI.Response,
    ) {
        AnvilGUI.Builder()
            .onComplete { player, text ->
                val input = extractValueFromText(text)
                val value = input.toDoubleOrNull() ?: return@onComplete AnvilGUI.Response.text("&f输入错误，您必须输入数字.")
                onComplete(player, value)
            }
            .onClose(onCancel)
            .text("")
            .itemLeft(getRequestItem())
            .title("&f[&5系统&f] &0${title}".colorize())
            .plugin(ECCore.instance)
            .open(opener)
    }
}