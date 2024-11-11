package kr.rendog.client.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

object MessageUtils {
    private val mc = MinecraftClient.getInstance()

    fun sendMessage(text: String) {
        val player = mc.player ?: return
        player.sendMessage(Text.literal(text))
    }

    fun sendErrorMessage(text: String) {
        sendMessage("§4${text}")
    }
}