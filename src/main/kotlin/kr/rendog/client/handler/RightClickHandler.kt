package kr.rendog.client.handler

import kr.rendog.client.data.CoolDownType
import kr.rendog.client.service.WeaponCoolService
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class RightClickHandler (
    private val weaponCoolService: WeaponCoolService
): UseItemCallback {

    private val passResult = TypedActionResult.pass(ItemStack.EMPTY)

    //This is fine because RendogServer plays in Adventure Mode so...
    override fun interact(player: PlayerEntity, world: World, hand: Hand): TypedActionResult<ItemStack> {
        if (player.isSpectator) return passResult
        if (hand != Hand.MAIN_HAND) return passResult
        if (!world.isClient) return passResult

        weaponCoolService.tryUpdate(player, player.inventory.mainHandStack, CoolDownType.RIGHT)
        return passResult
    }
}