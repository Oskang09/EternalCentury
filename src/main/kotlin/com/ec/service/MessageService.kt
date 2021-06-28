package com.ec.service

import com.ec.database.model.ChatType
import com.ec.util.ModelUtil.toDisplay
import com.ec.util.StringUtil.toComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.Template
import net.kyori.adventure.text.minimessage.transformation.TransformationType
import org.bukkit.entity.Player

@dev.reactant.reactant.core.component.Component
class MessageService {

    private val systemOnly = MiniMessage.builder().build()

    private val userOnly = MiniMessage.builder()
        .removeDefaultTransformations()
        .transformation(TransformationType.COLOR)
        .transformation(TransformationType.DECORATION)
        .build()

    fun userComponent(text: Component): Component {
        return userOnly.deserialize(userOnly.serialize(text))
    }

    fun plain(message: Component): String {
        return message.toString()
    }

    fun system(message: String): Component {
        return systemOnly.parse(
            "<#55FFFF>[<#AA00AA>系统<#55FFFF>] <reset><message>",
            Template.of("message", message.toComponent())
        )
    }

    fun private(from: Player,  message: String): Component {
        return systemOnly.parse("<#55FFFF>[<#5555FF>私讯<#55FFFF>] <reset><sender>: <#FFFFFF><message>",
            Template.of("sender", from.displayName()),
            Template.of("message", message.toComponent())
        )
    }

    fun playerChatPrefix(chatType: ChatType): Component {
        return systemOnly.parse("<#55FFFF>[<#55FF55><channel><#55FFFF>]",
            Template.of("channel", chatType.toDisplay())
        )
    }

    fun broadcast(message: String): Component {
        return systemOnly.parse("<#55FFFF>[<#55FF55><channel><#55FFFF>] <message>",
            Template.of("channel", ChatType.ANNOUNCEMENT.toDisplay()),
            Template.of("message", message.toComponent())
        )
    }

}