package kr.rendog.client.handler

import kr.rendog.client.service.WeaponCoolService
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartWorldTick
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld

class TickHandler (
    private val weaponCoolService: WeaponCoolService
): StartWorldTick {
    private val mc = MinecraftClient.getInstance()

    override fun onStartTick(world: ClientWorld?) {
        val player = mc.player ?: return
        weaponCoolService.handleLastSwap(player.inventory.selectedSlot)
    }
}