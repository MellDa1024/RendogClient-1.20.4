package kr.rendog.client.handler.chat

import kr.rendog.client.config.MainConfig
import kr.rendog.client.data.CoolDownType
import kr.rendog.client.service.WeaponCoolService
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import java.util.regex.Pattern

class LeftChatHandler (
    private val weaponCoolService: WeaponCoolService,
    private val config: MainConfig
): ClientReceiveMessageEvents.Game {
    companion object {
        private const val CHAT_DELAY = 150
    }

    private val cdPattern = Pattern.compile("([0-9.]*)초")
    private val cdMinPattern = Pattern.compile("([0-9]*)분 ([0-9.]*)초")

    override fun onReceiveGameMessage(message: Text, overlay: Boolean) {
        val leftClickChat = weaponCoolService.getLeftClickChat()
        if (leftClickChat == "") return
        weaponCoolService.resetLeftClickChat()
        val cooldown = message.siblings.getOrNull(12) ?: return
        if (cooldown.style.color?.name != "yellow") return
        if (!message.string.startsWith("   [ RD ]   재사용 대기시간이 ")) return
        if (System.currentTimeMillis() - weaponCoolService.getLastSwapTime() <= config.cooldownConfig.chatDelay) return

        val cooldownText = cooldown.string

        val patternedMessage = cdPattern.matcher(cooldownText)
        val patternedMessage2 = cdMinPattern.matcher(cooldownText)
        if (patternedMessage2.find()) {
            val value = patternedMessage2.group(2).toDouble() + patternedMessage2.group(1).toDouble() * 60
            weaponCoolService.tryUpdateFromChat(leftClickChat, CoolDownType.LEFT, value)
        } else if (patternedMessage.find()) {
            weaponCoolService.tryUpdateFromChat(leftClickChat, CoolDownType.LEFT, patternedMessage.group(1).toDouble())
        }
    }
}