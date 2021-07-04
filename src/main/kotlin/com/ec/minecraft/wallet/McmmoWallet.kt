package com.ec.minecraft.wallet

import com.ec.database.Wallet
import com.ec.manager.wallet.WalletAPI
import com.ec.manager.wallet.WalletManager
import com.ec.util.StringUtil.toComponent
import dev.reactant.reactant.extensions.itemMeta
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class McmmoWallet: WalletAPI(WalletManager.MCMMO_WALLET) {

    override fun getGrade(wallet: Wallet): Int {
        return 1
    }

    override fun getItemStack(wallet: Wallet): ItemStack {
        val stack = ItemStack(Material.BOOK)
        stack.itemMeta<ItemMeta> {
            displayName("&aMMO点数".toComponent())
            lore(arrayListOf(
                "&7&l --- &f&l点数介绍 &7&l--- ",
                "&f此点数为玩家MCMMO的总等级，就算季节刷新他都会永远保存",
                "&f玩家在每次升级的时候都会获得一点，所以一点也代表一个等级",
                "&f会在赛季结算才加入这个点数。",
                "&7&l --- &f&l点数咨询 &7&l--- ",
                "&f所有点数 - &e${wallet.total}",
                "&f剩余点数 - &e${wallet.balance}",
                "&f点数阶级 - &e${wallet.grade}"
            ).toComponent())
        }
        return stack
    }
}