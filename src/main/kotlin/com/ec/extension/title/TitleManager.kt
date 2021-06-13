package com.ec.extension.title

import com.ec.database.Players
import com.ec.database.Titles
import com.ec.extension.GlobalManager
import com.ec.util.StringUtil.generateUniqueID
import dev.reactant.reactant.core.component.Component
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.insert
import java.time.Instant
import java.util.*

@Component
class TitleManager {
    private val titles: MutableMap<String, TitleAPI> = HashMap();
    private lateinit var globalManager: GlobalManager

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager

        globalManager.reflections.loopTitles {
            it.initialize(globalManager)
            titles[it.id] = it
        }
    }

    fun getTitles(): Collection<TitleAPI> {
        return titles.values
    }

    fun checkPlayerTitleAvailability(player: Player) {
        val ecPlayer = globalManager.players.getByPlayer(player)
        ecPlayer.ensureUpdate("check player title availability", isAsync = true) {
            val playerTitles = ecPlayer.getTitles()
            titles.keys.intersect(playerTitles).forEach { titleKey ->
                val title = titles[titleKey]!!
                if (title.unlockCondition(ecPlayer)) {
                    Titles.insert {
                        it[id] = "".generateUniqueID()
                        it[playerId] = ecPlayer.database[Players.id]
                        it[titleId] = title.id
                        it[unlockedAt] = Instant.now().epochSecond
                    }
                    title.afterUnlock(ecPlayer)
                }
            }
        }
    }

    fun getPlayerActiveTitle(player: Player): TitleAPI? {
        val ecPlayer = globalManager.players.getByPlayer(player)
        return titles[ecPlayer.database[Players.currentTitle]]
    }
}
