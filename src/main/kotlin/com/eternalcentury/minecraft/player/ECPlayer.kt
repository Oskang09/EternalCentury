package com.eternalcentury.minecraft.player

import com.eternalcentury.config.PlayerData
import dev.reactant.reactant.core.component.Component
import dev.reactant.reactant.service.spec.config.Config
import org.bukkit.entity.Player
import java.util.*

@Component
data class ECPlayer(
    val player: Player,
    val config: Config<PlayerData>
) {
    private val uuid: UUID = player.uniqueId
    val data: PlayerData = config.content
}