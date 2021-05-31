package com.ec.extension.title

import com.ec.extension.GlobalManager
import dev.reactant.reactant.core.component.Component
import org.bukkit.entity.Player
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
        val ecPlayer = globalManager.players.getByPlayer(player)!!
        ecPlayer.ensureUpdate(
            { it ->
                titles.keys.intersect(ecPlayer.data.availableTitles.keys).forEach {
                    val title = titles[it]!!
                    if (title.unlockCondition(ecPlayer)) {
                        ecPlayer.data.availableTitles[title.id] = Instant.now().epochSecond
                    }
                }
                return@ensureUpdate it
            }
        )
    }

    fun getPlayerActiveTitle(player: Player): TitleAPI? {
        val ecPlayer = globalManager.players.getByPlayer(player)!!
        return titles[ecPlayer.data.currentTitle]
    }
}
