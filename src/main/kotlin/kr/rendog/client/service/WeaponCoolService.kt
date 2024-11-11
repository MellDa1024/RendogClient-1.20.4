package kr.rendog.client.service

import kr.rendog.client.RendogClient
import kr.rendog.client.data.CoolDownType
import kr.rendog.client.registry.WeaponCoolRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos

class WeaponCoolService(
    private val weaponCoolRegistry: WeaponCoolRegistry
) {
    private var moonLightName = ""
    private var rightClickChat = ""
    private var leftClickChat = ""

    private var lastSwapTime = System.currentTimeMillis()
    private var lastSlot = 0

    fun tryUpdate(player: PlayerEntity, weapon: ItemStack, type: CoolDownType) {
        val weaponName = weapon.name.string
        val cd = weaponCoolRegistry.find(weaponName)
        if (cd == null) weaponCoolRegistry.register(weaponName)
        when (type) {
            CoolDownType.RIGHT -> rightClickChat = weaponName
            CoolDownType.LEFT -> leftClickChat = weaponName
        }
        if (!checkVillageAndValidation(player, weaponName)) return
        if (type == CoolDownType.RIGHT) {
            if (weaponName.contains("문라이트") && !weaponName.contains("초월")) {
                moonLightName = weaponName
                return
            }
        }
        val currentCd = if (type == CoolDownType.LEFT) cd?.leftCD else cd?.rightCD
        if (currentCd == null || (currentCd - System.currentTimeMillis()) <= 0) update(weaponName, type, false)
    }

    fun tryUpdateFromChat(weaponName: String, type: CoolDownType, cooldown: Double? = null) {
        if (cooldown == null) update(weaponName, type, true)
        else update(weaponName, type, cooldown, true)
    }

    fun getLastSwapTime(): Long { return lastSwapTime }
    fun handleLastSwap(slot: Int) {
        if (lastSlot != slot) lastSwapTime = System.currentTimeMillis()
        lastSlot = slot
    }

    fun getMoonlightName(): String { return moonLightName }

    fun getRightClickChat(): String { return rightClickChat }
    fun getLeftClickChat(): String { return leftClickChat }

    fun resetMoonlightName() { moonLightName = "" }
    fun resetRightClickChat() { rightClickChat = "" }
    fun resetLeftClickChat() { leftClickChat = "" }

    private fun update(weaponName: String, type: CoolDownType, isChatDetection: Boolean) {
        val cooldown = WeaponDataService.getCD(weaponName, type)
        update(weaponName, type, cooldown, isChatDetection)
    }


    private fun update(weaponName: String, type: CoolDownType, cooldown: Double, isChatDetection: Boolean) {
        if (cooldown == 0.0) return
        val finalCooldown = if (isChatDetection) cooldown
        //TODO(CDR)
        else cooldown

        try {
            when (type) {
                CoolDownType.RIGHT -> weaponCoolRegistry.get(weaponName).rightCD = System.currentTimeMillis() + (1000 * finalCooldown).toLong()
                CoolDownType.LEFT -> weaponCoolRegistry.get(weaponName).leftCD = System.currentTimeMillis() + (1000 * finalCooldown).toLong()
            }
        } catch (e: Exception) {
            RendogClient.LOG.error("Exception while updating the weapon cooldown. WeaponName: $weaponName")
            e.printStackTrace()
        }
    }

    private fun checkVillageAndValidation(player: PlayerEntity, weaponName: String): Boolean {
        return ((player.world.spawnPos != BlockPos(278, 11, -134)) ||
                ((player.world.spawnPos == BlockPos(278, 11, -134)) && WeaponDataService.isAbleInVillage(weaponName)))
    }
}