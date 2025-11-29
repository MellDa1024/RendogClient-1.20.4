package kr.rendog.client.handler.chat

import kr.rendog.client.config.Config
import kr.rendog.client.data.CoolDownType
import kr.rendog.client.service.WeaponCoolService
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text
import java.util.regex.Pattern

class LeftChatHandler(
    private val weaponCoolService: WeaponCoolService
) : ClientReceiveMessageEvents.AllowGame {
    private val cdPattern = Pattern.compile("([0-9.]*)초")
    private val cdMinPattern = Pattern.compile("([0-9]*)분 ([0-9.]*)초")

    override fun allowReceiveGameMessage(message: Text, overlay: Boolean): Boolean {
        val leftClickChat = weaponCoolService.getLeftClickChat()
        if (leftClickChat == "") return true
        weaponCoolService.resetLeftClickChat()
        val cooldown = message.siblings.getOrNull(12) ?: return true
        if (cooldown.style.color?.name != "yellow") return true
        if (!message.string.startsWith("   [ RD ]   재사용 대기시간이 ")) return true
        if (System.currentTimeMillis() - weaponCoolService.getLastSwapTime() <= Config.cooldownChatDelay) return false

        val cooldownText = cooldown.string

        val patternedMessage = cdPattern.matcher(cooldownText)
        val patternedMessage2 = cdMinPattern.matcher(cooldownText)
        if (patternedMessage2.find()) {
            val value = patternedMessage2.group(2).toDouble() + patternedMessage2.group(1).toDouble() * 60
            weaponCoolService.tryUpdateFromChat(leftClickChat, CoolDownType.LEFT, value)
            return false
        } else if (patternedMessage.find()) {
            weaponCoolService.tryUpdateFromChat(leftClickChat, CoolDownType.LEFT, patternedMessage.group(1).toDouble())
            return false
        }
        return true
    }
}