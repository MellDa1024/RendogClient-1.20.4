package kr.rendog.client.handler

import kr.rendog.client.data.CoolDownType
import kr.rendog.client.service.WeaponCoolService
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity

class LeftClickHandler (
    private val weaponCoolService: WeaponCoolService
): ClientPreAttackCallback {
    override fun onClientPlayerPreAttack(client: MinecraftClient, player: ClientPlayerEntity, clickCount: Int): Boolean {
        if (clickCount == 0) return false
        weaponCoolService.tryUpdate(player, player.inventory.mainHandStack, CoolDownType.LEFT)
        return false
    }
}