package kr.rendog.client.handler.chat

import kr.rendog.client.data.CoolDownType
import kr.rendog.client.service.WeaponCoolService
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text
import java.util.regex.Pattern

class RightChatHandler (
    private val weaponCoolService: WeaponCoolService
): ClientReceiveMessageEvents.Game {
    private val cdPattern = Pattern.compile("([0-9.]*)초")
    private val cdMinPattern = Pattern.compile("([0-9]*)분 ([0-9.]*)초")

    override fun onReceiveGameMessage(message: Text, overlay: Boolean) {
        val rightClickChat = weaponCoolService.getRightClickChat()
        if (rightClickChat == "") return
        weaponCoolService.resetRightClickChat()
        val cooldown = message.siblings.getOrNull(12) ?: return
        if (cooldown.style.color?.name != "red") return
        if (!message.string.startsWith("   [ RD ]   재사용 대기시간이 ")) return

        val cooldownText = cooldown.string

        val patternedMessage = cdPattern.matcher(cooldownText)
        val patternedMessage2 = cdMinPattern.matcher(cooldownText)
        if (patternedMessage2.find()) {
            val value = patternedMessage2.group(2).toDouble() + patternedMessage2.group(1).toDouble() * 60
            weaponCoolService.tryUpdateFromChat(rightClickChat, CoolDownType.RIGHT, value)
        } else if (patternedMessage.find()) {
            weaponCoolService.tryUpdateFromChat(rightClickChat, CoolDownType.RIGHT, patternedMessage.group(1).toDouble())
        }
    }
}