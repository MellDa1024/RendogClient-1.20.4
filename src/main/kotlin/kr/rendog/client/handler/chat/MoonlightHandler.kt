package kr.rendog.client.handler.chat

import kr.rendog.client.data.CoolDownType
import kr.rendog.client.service.WeaponCoolService
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Text

class MoonlightHandler (
    private val weaponCoolService: WeaponCoolService
): ClientReceiveMessageEvents.Game {
    override fun onReceiveGameMessage(message: Text, overlay: Boolean) {
        val moonlightName = weaponCoolService.getMoonlightName()
        if (moonlightName.isEmpty()) return
        if (message.string.trim() == "문라이트가 영혼을 방출합니다!") {
            weaponCoolService.tryUpdateFromChat(moonlightName, CoolDownType.RIGHT)
            weaponCoolService.resetMoonlightName()
        }
    }
}