package com.ec.util

import com.ec.ECCore
import org.bukkit.Bukkit
import java.io.File
import java.net.URL

object ClassUtil {

    inline fun <reified C> findClassAndRun(pkgName: String, run: (C) -> Unit) {
        var name = pkgName
        if (!name.startsWith("/")) {
            name = "/$name"
        }
        name = name.replace('.', '/')

        val url: URL = ECCore::class.java.getResource(name)!!
        val directory = File(url.file)

        Bukkit.getLogger().info(url.file)
        if (directory.exists()) {
            directory.walk()
                .filter { f -> f.isFile && !f.name.contains('$') && f.name.endsWith(".class") }
                .forEach {
                    val fullyQualifiedClassName = pkgName +
                            it.canonicalPath.removePrefix(directory.canonicalPath)
                                .dropLast(6) // remove .class
                                .replace('/', '.')

                    val clazz = Class.forName(fullyQualifiedClassName)
                    Bukkit.getLogger().info(fullyQualifiedClassName)
                    if (C::class.java.isAssignableFrom(clazz)) {
                        val pl = clazz.asSubclass(C::class.java)
                        val module = pl.newInstance()
                        run(module)
                    }
                }
            }
        }
    }