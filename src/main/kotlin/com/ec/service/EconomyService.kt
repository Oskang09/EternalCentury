package com.ec.service

import com.ec.database.Players
import com.ec.manager.GlobalManager
import com.ec.manager.wallet.WalletManager
import com.ec.util.DoubleUtil.roundTo
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.reactant.core.component.Component
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

@Component
class EconomyService: Economy {

    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getName(): String {
        return "Economy"
    }

    override fun hasBankSupport(): Boolean {
        return false
    }

    override fun fractionalDigits(): Int {
        return -1
    }

    override fun format(amount: Double): String {
        return amount.roundTo(2).toString()
    }

    override fun currencyNamePlural(): String {
        return "金币"
    }

    override fun currencyNameSingular(): String {
        return "金币"
    }

    override fun hasAccount(playerName: String): Boolean {
        return globalManager.players.getByPlayerName(playerName) != null
    }

    override fun hasAccount(player: OfflinePlayer): Boolean {
        return globalManager.players.getByOfflinePlayer(player) != null
    }

    override fun hasAccount(playerName: String, worldName: String): Boolean {
        return hasAccount(playerName)
    }

    override fun hasAccount(player: OfflinePlayer, worldName: String): Boolean {
        return hasAccount(player)
    }

    override fun getBalance(playerName: String): Double {
        return globalManager.wallets.playerWallet(playerName, WalletManager.ECONOMY_WALLET).balance
    }

    override fun getBalance(player: OfflinePlayer): Double {
        return globalManager.wallets.playerWallet(player.name!!, WalletManager.ECONOMY_WALLET).balance
    }

    override fun getBalance(playerName: String, world: String): Double {
        return getBalance(playerName)
    }

    override fun getBalance(player: OfflinePlayer, world: String): Double {
        return getBalance(player)
    }

    override fun has(playerName: String, amount: Double): Boolean {
        return globalManager.wallets.playerHas(playerName, WalletManager.ECONOMY_WALLET, amount)
    }

    override fun has(player: OfflinePlayer, amount: Double): Boolean {
        return has(player.name!!, amount)
    }

    override fun has(playerName: String, worldName: String?, amount: Double): Boolean {
        return has(playerName, amount)
    }

    override fun has(player: OfflinePlayer, worldName: String?, amount: Double): Boolean {
        return has(player, amount)
    }

    override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse {
        val wallet = globalManager.wallets.withdrawPlayerWallet(playerName, WalletManager.ECONOMY_WALLET, amount)
        return EconomyResponse(
            amount,
            wallet.balance,
            EconomyResponse.ResponseType.SUCCESS,
            ""
        )
    }

    override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        return withdrawPlayer(player.name!!, amount)
    }

    override fun withdrawPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse {
        return withdrawPlayer(playerName, amount)
    }

    override fun withdrawPlayer(player: OfflinePlayer, worldName: String, amount: Double): EconomyResponse {
        return withdrawPlayer(player, amount)
    }

    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse {
        val wallet = globalManager.wallets.depositPlayerWallet(playerName, WalletManager.ECONOMY_WALLET, amount)
        return EconomyResponse(
            amount,
            wallet.balance,
            EconomyResponse.ResponseType.SUCCESS,
            ""
        )
    }

    override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        return depositPlayer(player.name!!, amount)
    }

    override fun depositPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse {
        return depositPlayer(playerName, amount)
    }

    override fun depositPlayer(player: OfflinePlayer, worldName: String, amount: Double): EconomyResponse {
        return depositPlayer(player, amount)
    }

    // By default account will create when join the game
    override fun createPlayerAccount(playerName: String): Boolean {
        return true
    }

    // By default account will create when join the game
    override fun createPlayerAccount(player: OfflinePlayer?): Boolean {
        return true
    }

    // By default account will create when join the game
    override fun createPlayerAccount(playerName: String?, worldName: String?): Boolean {
        return true
    }

    // By default account will create when join the game
    override fun createPlayerAccount(player: OfflinePlayer?, worldName: String?): Boolean {
        return true
    }

    override fun createBank(name: String?, player: String?): EconomyResponse {
        return EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun createBank(name: String?, player: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun deleteBank(name: String?): EconomyResponse {
        return EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun bankBalance(name: String?): EconomyResponse {
        return EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun bankHas(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun bankWithdraw(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun bankDeposit(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun isBankOwner(name: String?, playerName: String?): EconomyResponse {
        return EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun isBankOwner(name: String?, player: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun isBankMember(name: String?, playerName: String?): EconomyResponse {
        return EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun isBankMember(name: String?, player: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0,0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun getBanks(): MutableList<String> {
        return mutableListOf()
    }
}