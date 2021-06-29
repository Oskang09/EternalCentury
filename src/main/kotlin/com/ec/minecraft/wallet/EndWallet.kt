package com.ec.minecraft.wallet

import com.ec.database.Wallet
import com.ec.manager.wallet.WalletAPI
import com.ec.manager.wallet.WalletManager
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class EndWallet: WalletAPI(WalletManager.END_WALLET) {

    override fun getItemStack(wallet: Wallet): ItemStack {
        val stack = ItemStack(Material.END_PORTAL_FRAME)
        stack.itemMeta<ItemMeta> {
            displayName("&f末地点数".toComponent())
            lore(arrayListOf(
                "&7&l --- &f&l点数介绍 &7&l--- ",
                "&f此点数只有在末地内才能通过物品兑换",
                "&f拥有点数后能兑换一些末地专有物品",
                "&7&l --- &f&l点数咨询 &7&l--- ",
                "&f所有点数 - &e${wallet.total}",
                "&f剩余点数 - &e${wallet.balance}",
                "&f点数阶级 - &e${wallet.grade}"
            ).toComponent())
        }
        return stack
    }

    override fun getGrade(wallet: Wallet): Int {
        return 1
    }

}