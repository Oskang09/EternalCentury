package com.ec.minecraft.inventory.filter

import com.ec.manager.inventory.UIBase
import com.ec.manager.inventory.UIProvider
import com.ec.util.StringUtil.toColorized
import dev.reactant.resquare.dom.Component
import dev.reactant.resquare.dom.childrenOf
import dev.reactant.resquare.dom.declareComponent
import dev.reactant.resquare.elements.DivProps
import dev.reactant.resquare.elements.div
import dev.reactant.resquare.elements.styleOf
import dev.reactant.resquare.render.useCancelRawEvent
import org.bukkit.entity.HumanEntity
import java.lang.Exception

class YesOrNoUI: UIProvider<YesOrNoUI.YesOrNoUIProps>("yesorno") {

    data class YesOrNoUIProps(
        val title: String,
        val onYes: () -> Unit,
        val onNo: () -> Unit,
    )

    override fun info(props: YesOrNoUIProps): UIBase {
        return UIBase(
            rows = 1,
            title = props.title.toColorized(),
            closable = false,
        )
    }

    override fun props(player: HumanEntity): YesOrNoUIProps {
        throw Exception("invalid access yesornoui without props")
    }

    override val render = declareComponent<YesOrNoUIProps> { props ->
        useCancelRawEvent()

        div(DivProps(
            style = styleOf {
                width = 100.percent
                height = 100.percent
            },
            children = childrenOf(
                div(DivProps(
                    style = styleOf {
                        marginLeft = 2.px
                        width = 1.px
                        height = 1.px
                    },
                    item = globalManager.component.woolAccept(),
                    onClick = { props.onYes() }
                )),
                div(DivProps(
                    style = styleOf {
                        marginLeft = 3.px
                        width = 1.px
                        height = 1.px
                    },
                    item = globalManager.component.woolDecline(),
                    onClick = { props.onNo() }
                )),
            )
        ))
    }
}