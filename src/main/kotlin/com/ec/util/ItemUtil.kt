package com.ec.util

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object ItemUtil {

    fun ItemStack.toBas64(): String {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)
        dataOutput.writeObject(this)

        dataOutput.close()
        return Base64Coder.encodeLines(outputStream.toByteArray())
    }

    fun String.fromBase64(): ItemStack {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(this))
        val dataInput = BukkitObjectInputStream(inputStream)
        dataInput.use {
            return it.readObject() as ItemStack
        }
    }
}