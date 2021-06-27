package com.ec.manager.packet

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.ec.manager.GlobalManager
import dev.reactant.reactant.core.component.Component

@Component
class PacketManager {
    private lateinit var globalManager: GlobalManager
    private lateinit var protocolManager: ProtocolManager
    private val emptyWrappedChatComponent = WrappedChatComponent.fromText("")

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
        this.protocolManager = ProtocolLibrary.getProtocolManager()
    }
}