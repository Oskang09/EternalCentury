package com.ec.manager.wallet

import com.ec.database.Players
import com.ec.database.Wallet
import com.ec.database.WalletHistories
import com.ec.database.Wallets
import com.ec.database.enums.WalletAction
import com.ec.manager.GlobalManager
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.reactant.core.component.Component
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.util.*

@Component
class WalletManager {

    companion object {
        const val ECONOMY_WALLET = "economy"
        const val DONATOR_WALLET = "donator"
        const val ACTIVITY_WALLET = "activity"
        const val END_WALLET = "end"
        const val MCMMO_WALLET = "mcmmo"
        const val NETHER_WALLET = "nether"
        const val SEASON_WALLET = "season"
    }

    private val wallets: MutableMap<String, WalletAPI> = mutableMapOf()
    private lateinit var globalManager: GlobalManager
    private var mutex = Mutex()

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.reflections.loopWallets {
            it.initialize(globalManager)
            wallets[it.id] = it
        }
    }

    fun getWallets(): MutableMap<String, WalletAPI> {
        return wallets
    }

    fun playerWallet(playerName: String, walletName: String): Wallet {
        val ecPlayer = globalManager.players.getByPlayerName(playerName)!!
        val dbWallet = transaction {
            Wallets
                .select { Wallets.playerId eq ecPlayer[Players.id] }
                .andWhere { Wallets.type eq walletName }
                .singleOrNull()
        } ?: return Wallet(type = walletName)
        return Wallet(dbWallet)
    }

    fun playerHas(playerName: String, walletName: String, balance: Double): Boolean {
        return playerWallet(playerName, walletName).balance >= balance
    }

    fun playerHas(player: Player, walletName: String, balance: Double): Boolean {
        return playerHas(player.name, walletName, balance)
    }

    fun withdrawPlayerWallet(playerName: String, walletName: String, amount: Double): Wallet {
        val player = globalManager.players.getByPlayerName(playerName)!!
        val wallet = playerWallet(playerName, walletName)
        runBlocking {
            transaction {
                WalletHistories.insert {
                    it[id] = "".generateUniqueID()
                    it[playerId] = player[Players.id]
                    it[action] = WalletAction.WITHDRAW
                    it[type] = walletName
                    it[actionAt] = Instant.now().epochSecond
                    it[balance] = amount
                }
            }

            wallet.balance = wallet.balance - amount
            wallet.total = transaction {
                WalletHistories
                    .select { WalletHistories.playerId eq player[Players.id] }
                    .andWhere { WalletHistories.action eq WalletAction.DEPOSIT }
                    .sumOf { it[WalletHistories.balance] }
            }
            wallet.grade = getGradeByWallet(walletName, wallet)

            mutex.withLock(playerName + wallet.type) {
                transaction {
                    when (wallet.id == "") {
                        true -> {
                            Wallets.insert {
                                it[id] = "".generateUniqueID()
                                it[playerId] = player[Players.id]
                                it[type] = walletName
                                it[grade] = wallet.grade
                                it[total] = wallet.total
                                it[balance] = wallet.balance
                                it[updatedAt] = Instant.now().epochSecond
                            }
                        }
                        false -> {
                            Wallets.update({ Wallets.id eq wallet.id }) {
                                it[type] = walletName
                                it[grade] = wallet.grade
                                it[total] = wallet.total
                                it[balance] = wallet.balance
                                it[updatedAt] = Instant.now().epochSecond
                            }
                        }
                    }
                }
            }
            globalManager.players.refreshPlayerIfOnline(UUID.fromString(player[Players.uuid]))
        }
        return wallet
    }

    fun withdrawPlayerWallet(player: Player, walletName: String, amount: Double): Wallet {
        return withdrawPlayerWallet(player.name, walletName, amount)
    }

    fun depositPlayerWallet(playerName: String, walletName: String, amount: Double): Wallet {
        val player = globalManager.players.getByPlayerName(playerName)!!
        val wallet = playerWallet(playerName, walletName)
        runBlocking {
            transaction {
                WalletHistories.insert {
                    it[id] = "".generateUniqueID()
                    it[playerId] = player[Players.id]
                    it[action] = WalletAction.DEPOSIT
                    it[type] = walletName
                    it[actionAt] = Instant.now().epochSecond
                    it[balance] = amount
                }
            }

            wallet.balance = wallet.balance + amount
            wallet.total = transaction {
                WalletHistories
                    .select { WalletHistories.playerId eq player[Players.id] }
                    .andWhere { WalletHistories.action eq WalletAction.DEPOSIT }
                    .sumOf { it[WalletHistories.balance] }
            }
            wallet.grade = getGradeByWallet(walletName, wallet)

            mutex.withLock(playerName + wallet.type) {
                transaction {
                    when (wallet.id == "") {
                        true -> {
                            Wallets.insert {
                                it[id] = "".generateUniqueID()
                                it[playerId] = player[Players.id]
                                it[type] = walletName
                                it[grade] = wallet.grade
                                it[total] = wallet.total
                                it[balance] = wallet.balance
                                it[updatedAt] = Instant.now().epochSecond
                            }
                        }
                        false -> {
                            Wallets.update({ Wallets.id eq wallet.id }) {
                                it[type] = walletName
                                it[grade] = wallet.grade
                                it[total] = wallet.total
                                it[balance] = wallet.balance
                                it[updatedAt] = Instant.now().epochSecond
                            }
                        }
                    }
                }
            }
            globalManager.players.refreshPlayerIfOnline(UUID.fromString(player[Players.uuid]))
        }
        return wallet
    }

    fun depositPlayerWallet(player: Player, walletName: String, amount: Double): Wallet {
        return depositPlayerWallet(player.name, walletName, amount)
    }

    fun getGradeByWallet(walletName: String, wallet: Wallet): Int {
        val api = wallets[walletName]!!
        return api.getGrade(wallet)
    }
}