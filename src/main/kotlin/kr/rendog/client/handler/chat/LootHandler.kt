package kr.rendog.client.handler.chat

import kr.rendog.client.config.Config
import kr.rendog.client.service.LootPerMinuteService
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text

class LootHandler (
    private val lootPerMinuteService: LootPerMinuteService
): ClientReceiveMessageEvents.AllowGame {
    override fun allowReceiveGameMessage(message: Text, overlay: Boolean): Boolean {
        if (!message.string.startsWith("   [ RD ]   ") || !message.string.endsWith("전리품을 획득했습니다.")) return true
        val isRare = message.string.startsWith("   [ RD ]   [RARE] ")
        lootPerMinuteService.addCount()
        return !Config.lootPerMinuteCancelLootChat || isRare
    }
}