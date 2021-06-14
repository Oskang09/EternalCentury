package com.ec.minecraft.inventory

import com.ec.extension.inventory.UIBase
import com.ec.extension.inventory.UIProvider
import com.ec.util.StringUtil.colorize
import dev.reactant.resquare.dom.Component
import org.bukkit.entity.HumanEntity

class PaymentUI: UIProvider<PaymentUI.PaymentUIProps>("payment") {

    data class PaymentUIProps(
        val methods: List<String> = listOf()
    )

    override fun info(props: PaymentUIProps): UIBase {
        return UIBase(
            rows = 3,
            cols = 9,
            title = "&b[&5系统&b] &6伺服赞助".colorize()
        )
    }

    override fun props(player: HumanEntity): PaymentUIProps {
        return PaymentUIProps()
    }

    override val render: Component.WithProps<PaymentUIProps>
        get() = TODO("Not yet implemented")
}