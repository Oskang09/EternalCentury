package com.ec.service

import com.ec.database.Economies
import com.ec.database.Players
import com.ec.database.model.economy.EconomyInfo
import com.ec.database.model.economy.EconomyType
import com.ec.extension.GlobalManager
import com.ec.logger.Logger
import com.ec.util.DoubleUtil.roundTo
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.reactant.core.component.Component
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
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
        val ecPlayer = globalManager.players.getByPlayerName(playerName) ?: return 0.0
        return ecPlayer[Players.balance].balance
    }

    override fun getBalance(player: OfflinePlayer): Double {
        val ecPlayer = globalManager.players.getByOfflinePlayer(player) ?: return 0.0
        return ecPlayer[Players.balance].balance
    }

    override fun getBalance(playerName: String, world: String): Double {
        return getBalance(playerName)
    }

    override fun getBalance(player: OfflinePlayer, world: String): Double {
        return getBalance(player)
    }

    override fun has(playerName: String, amount: Double): Boolean {
        val ecPlayer = globalManager.players.getByPlayerName(playerName) ?: return false
        val balance = ecPlayer[Players.balance].balance
        return balance >= amount

    }

    override fun has(player: OfflinePlayer, amount: Double): Boolean {
        val ecPlayer = globalManager.players.getByOfflinePlayer(player) ?: return false
        val balance = ecPlayer[Players.balance].balance
        return balance >= amount
    }

    override fun has(playerName: String, worldName: String?, amount: Double): Boolean {
        return has(playerName, amount)
    }

    override fun has(player: OfflinePlayer, worldName: String?, amount: Double): Boolean {
        return has(player, amount)
    }

    override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse {
        return transaction {
            val ecPlayer = globalManager.players.getByPlayerName(playerName)!!

            Economies.insert {
                it[id] = "".generateUniqueID()
                it[playerId] = ecPlayer[Players.id]
                it[type] = EconomyType.WITHDRAW
                it[balance] = amount
                it[actionAt] = Instant.now().epochSecond
            }

            val nextBalance = ecPlayer[Players.balance].balance - amount
            val total = Economies
                .select { Economies.playerId eq ecPlayer[Players.id] }
                .andWhere { Economies.type eq EconomyType.DEPOSIT }
                .sumOf { it[Economies.balance] }

            Players.update({ Players.id eq ecPlayer[Players.id] }) {
                it[balance] = EconomyInfo(
                    total,
                    nextBalance,
                    Instant.now().epochSecond
                )
            }

            globalManager.players.refreshPlayerIfOnline(UUID.fromString(ecPlayer[Players.uuid]!!))
            return@transaction EconomyResponse(
                amount,
                nextBalance,
                EconomyResponse.ResponseType.SUCCESS,
                ""
            )
        }
    }

    override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        return transaction {
            val ecPlayer = globalManager.players.getByOfflinePlayer(player)!!

            Economies.insert {
                it[id] = "".generateUniqueID()
                it[playerId] = ecPlayer[Players.id]
                it[type] = EconomyType.WITHDRAW
                it[balance] = amount
                it[actionAt] = Instant.now().epochSecond
            }

            val nextBalance = ecPlayer[Players.balance].balance - amount
            val total = Economies
                .select { Economies.playerId eq ecPlayer[Players.id] }
                .andWhere { Economies.type eq EconomyType.DEPOSIT }
                .sumOf { it[Economies.balance] }

            Players.update({ Players.id eq ecPlayer[Players.id] }) {
                it[balance] = EconomyInfo(
                    total,
                    nextBalance,
                    Instant.now().epochSecond
                )
            }

            globalManager.players.refreshPlayerIfOnline(UUID.fromString(ecPlayer[Players.uuid]!!))
            return@transaction EconomyResponse(
                amount,
                nextBalance,
                EconomyResponse.ResponseType.SUCCESS,
                ""
            )
        }
    }

    override fun withdrawPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse {
        return withdrawPlayer(playerName, amount)
    }

    override fun withdrawPlayer(player: OfflinePlayer, worldName: String, amount: Double): EconomyResponse {
        return withdrawPlayer(player, amount)
    }

    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse {
        return transaction {
            val ecPlayer = globalManager.players.getByPlayerName(playerName)!!

            Economies.insert {
                it[id] = "".generateUniqueID()
                it[playerId] = ecPlayer[Players.id]
                it[type] = EconomyType.DEPOSIT
                it[balance] = amount
                it[actionAt] = Instant.now().epochSecond
            }

            val nextBalance = ecPlayer[Players.balance].balance + amount
            val total = Economies
                .select { Economies.playerId eq ecPlayer[Players.id] }
                .andWhere { Economies.type eq EconomyType.DEPOSIT }
                .sumOf { it[Economies.balance] }

            Players.update({ Players.id eq ecPlayer[Players.id] }) {
                it[balance] = EconomyInfo(
                    total,
                    nextBalance,
                    Instant.now().epochSecond
                )
            }

            globalManager.players.refreshPlayerIfOnline(UUID.fromString(ecPlayer[Players.uuid]!!))
            return@transaction EconomyResponse(
                amount,
                nextBalance,
                EconomyResponse.ResponseType.SUCCESS,
                ""
            )
        }
    }

    override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse {
        return transaction {
            val ecPlayer = globalManager.players.getByOfflinePlayer(player)!!

            Economies.insert {
                it[id] = "".generateUniqueID()
                it[playerId] = ecPlayer[Players.id]
                it[type] = EconomyType.DEPOSIT
                it[balance] = amount
                it[actionAt] = Instant.now().epochSecond
            }

            val nextBalance = ecPlayer[Players.balance].balance + amount
            val total = Economies
                .select { Economies.playerId eq ecPlayer[Players.id] }
                .andWhere { Economies.type eq EconomyType.DEPOSIT }
                .sumOf { it[Economies.balance] }

            Players.update({ Players.id eq ecPlayer[Players.id] }) {
                it[balance] = EconomyInfo(
                    total,
                    nextBalance,
                    Instant.now().epochSecond
                )
            }

            globalManager.players.refreshPlayerIfOnline(UUID.fromString(ecPlayer[Players.uuid]!!))
            return@transaction EconomyResponse(
                amount,
                nextBalance,
                EconomyResponse.ResponseType.SUCCESS,
                ""
            )
        }
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