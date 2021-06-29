package com.ec.minecraft.wallet

import com.ec.database.Wallet
import com.ec.manager.wallet.WalletAPI
import com.ec.manager.wallet.WalletManager
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ActivityWallet: WalletAPI(WalletManager.ACTIVITY_WALLET) {

    override fun getItemStack(wallet: Wallet): ItemStack {
        val stack = ItemStack(Material.ZOMBIE_HEAD)
        stack.itemMeta<ItemMeta> {
            displayName("&f活动点数".toComponent())
            lore(arrayListOf(
                "&7&l --- &f&l点数介绍 &7&l--- ",
                "&f此点数只有参与活动才能获得哟",
                "&f拥有点数后能兑换一些罕见珍贵的物品",
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