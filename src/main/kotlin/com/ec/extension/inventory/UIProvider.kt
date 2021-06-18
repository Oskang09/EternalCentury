package com.ec.extension.inventory

import com.ec.extension.GlobalManager
import dev.reactant.resquare.bukkit.container.createUI
import dev.reactant.resquare.dom.Component
import org.bukkit.entity.HumanEntity

abstract class UIProvider<T : Any>(val id: String) {
    protected lateinit var globalManager: GlobalManager
    open fun initialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
    }

    protected abstract fun info(props: T): UIBase
    protected abstract fun props(player: HumanEntity): T

    private var staticProps: T? = null
    open val isStaticProps: Boolean = false

    protected abstract val render: Component.WithProps<T>

    fun displayTo(player: HumanEntity, props: T) {
        val base = info(props)
        val container = createUI(
            render, props, base.cols, base.rows, base.title,
            multiThreadComponentRender = true,
            multiThreadStyleRender = true,
            autoDestroy = true
        )

        container.openInventory(player)
    }

    fun displayTo(player: HumanEntity) {
        var props: T?
        if (isStaticProps) {
            if (staticProps == null) {
                this.staticProps = props(player)
            }
            props = this.staticProps
        } else {
            props = props(player)
        }

        displayTo(player, props!!)
    }

}