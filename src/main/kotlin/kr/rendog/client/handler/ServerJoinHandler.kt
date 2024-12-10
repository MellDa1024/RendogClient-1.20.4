package kr.rendog.client.handler

import kr.rendog.client.RendogClient
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.text.Text

class ServerJoinHandler: Join {
    private val rendogServerIp = arrayOf("rendog.kr", "global.rendog.kr", "private-scn-sev99.scn.pw:1799")

    override fun onPlayReady(handler: ClientPlayNetworkHandler, sender: PacketSender, client: MinecraftClient) {
        val address = handler.serverInfo?.address
        if (address == null || !rendogServerIp.contains(address.lowercase())) {
            RendogClient.LOG.info("Player is not in RendogServer! Disconnecting, it may crash or bricked in SinglePlayer...")
            handler.onDisconnected(Text.literal("RendogClient is only available in RendogServer."))
        }
    }
}