package com.ec.manager.packet

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.ec.ECCore
import com.ec.manager.GlobalManager
import dev.reactant.reactant.core.component.Component
import org.bukkit.Bukkit
import org.inventivetalent.packetlistener.PacketListenerAPI
import org.inventivetalent.packetlistener.handler.PacketHandler
import org.inventivetalent.packetlistener.handler.ReceivedPacket

import org.inventivetalent.packetlistener.handler.SentPacket




@Component
class PacketManager {
    private lateinit var globalManager: GlobalManager
    private lateinit var protocolManager: ProtocolManager
    private val emptyWrappedChatComponent = WrappedChatComponent.fromText("")

    fun onInitialize(globalManager: GlobalManager) {
        this.globalManager = globalManager
        this.protocolManager = ProtocolLibrary.getProtocolManager()

        val packetApi = PacketListenerAPI()
        packetApi.load()
        packetApi.init(ECCore.instance)

//        PacketListenerAPI.addPacketHandler(object : PacketHandler(ECCore.instance) {
//            override fun onSend(packet: SentPacket) {
//                if (packet.playername != null) {
//                    Bukkit.getLogger().info("=========== onSend =============")
//                    Bukkit.getLogger().info("PacketName: ${packet.packetName}")
//                    Bukkit.getLogger().info("PlayerName: ${packet.playername}")
//                    Bukkit.getLogger().info("Packet: $packet")
//                    Bukkit.getLogger().info("Packet,Packet: ${packet.packet}")
//                    Bukkit.getLogger().info("isCancelled: ${packet.isCancelled}")
//                    Bukkit.getLogger().info("================================")
//                }
//            }
//
//            override fun onReceive(packet: ReceivedPacket) {
//                if (packet.playername != null) {
//                    Bukkit.getLogger().info("=========== onReceive =============")
//                    Bukkit.getLogger().info("PacketName: ${packet.packetName}")
//                    Bukkit.getLogger().info("PlayerName: ${packet.playername}")
//                    Bukkit.getLogger().info("Packet: $packet")
//                    Bukkit.getLogger().info("Packet,Packet: ${packet.packet}")
//                    Bukkit.getLogger().info("isCancelled: ${packet.isCancelled}")
//                    Bukkit.getLogger().info("===================================")
//                }
//            }
//        })
    }
}