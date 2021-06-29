package com.ec.minecraft.inventory.container

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.component.PaginationItem
import com.ec.manager.inventory.component.PaginationUI
import com.ec.manager.inventory.component.PaginationUIProps
import com.ec.util.StringUtil.toComponent
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

class WalletUI: PaginationUI<Unit>("wallet") {

    override fun info(props: PaginationUIProps): UIBase {
        return UIBase(
            title = "&b[&5系统&b] &6钱包咨询"
        )
    }

    override fun props(player: HumanEntity): PaginationUIProps {
        val allWallets = globalManager.wallets.getWallets()
        return PaginationUIProps(
            info = globalManager.component.item(Material.DIAMOND) {
                it.displayName("&b[&5系统&b] &6钱包咨询".toComponent())
                it.lore(arrayListOf("&7点数数量 &f- &a${allWallets.size}").toComponent())
            },
            items = {
                allWallets.map { (name, wallet) ->
                    val playerWallet = globalManager.wallets.playerWallet(player.name, name)
                    return@map PaginationItem(item = wallet.getItemStack(playerWallet))
                }
            }
        )
    }
}