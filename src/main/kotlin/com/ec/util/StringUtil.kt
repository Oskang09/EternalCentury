package com.ec.util

import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.text.SimpleDateFormat
import java.util.*

object StringUtil {
    private val standardMessage = MiniMessage.builder()
        .build()
    private val UNIQUE_ID_FORMAT: SimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmssSS")


    fun com.loohp.interactivechat.libs.net.kyori.adventure.text.Component.toComponent(): Component {
        val json = GsonComponentSerializer.gson().serialize(this)
        return net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(json)
    }

    fun String.toComponent(): Component {
        if (this == "") {
            return Component.empty()
        }
        return standardMessage.parse(this.replace("&", "§"))
    }

    // Only used for legacy plugins
    fun String.toColorized(): String {
        return this.replace("&", "§")
    }

    fun List<String>.toComponent(): List<Component> {
        return this.map { it.toComponent() }
    }

    fun String.generateUniqueID(): String {
        val id = UNIQUE_ID_FORMAT.format(Date())
        Thread.sleep(1)
        return id
    }

}