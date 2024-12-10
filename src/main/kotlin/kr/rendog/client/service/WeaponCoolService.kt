package kr.rendog.client.service

import kr.rendog.client.RendogClient
import kr.rendog.client.data.CoolDownType
import kr.rendog.client.registry.WeaponCoolRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos

class WeaponCoolService(
    private val weaponCoolRegistry: WeaponCoolRegistry,
    private val weaponDataService: WeaponDataService
) {
    private var moonLightName = ""
    private var rightClickChat = ""
    private var leftClickChat = ""

    private var lastSwapTime = System.currentTimeMillis()
    private var lastSlot = 0

    fun tryUpdate(player: PlayerEntity, weapon: ItemStack, type: CoolDownType) {
        val weaponName = weapon.name.string
        val group = weaponDataService.getGroup(weaponName)
        val cd = weaponCoolRegistry.find(group)
        if (cd == null) weaponCoolRegistry.register(group)
        when (type) {
            CoolDownType.RIGHT -> rightClickChat = weaponName
            CoolDownType.LEFT -> leftClickChat = weaponName
        }
        if (!checkVillageValidation(player, weaponName)) return
        if (type == CoolDownType.RIGHT) {
            if (weaponName.contains("문라이트") && !weaponName.contains("초월")) {
                moonLightName = weaponName
                return
            }
        }
        val lastUsed = if (type == CoolDownType.LEFT) cd?.leftCD else cd?.rightCD
        if (lastUsed == null || (System.currentTimeMillis() - lastUsed) > (weaponDataService.getFinalWeaponCooldown(weaponName, type) * 1000)) {
            update(group, type, System.currentTimeMillis())
        }
    }

    fun tryUpdateFromChat(weaponName: String, type: CoolDownType, cooldown: Double?) {
        val group = weaponDataService.getGroup(weaponName)
        if (cooldown == null) update(group, type, System.currentTimeMillis())
        else {
            val lastUsed = weaponDataService.getFinalWeaponCooldown(weaponName, type) - cooldown
            update(group, type, System.currentTimeMillis() - (lastUsed * 1000).toLong())
        }
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

    private fun update(group: String, type: CoolDownType, time: Long) {
        try {
            when (type) {
                CoolDownType.RIGHT -> weaponCoolRegistry.get(group).rightCD = time
                CoolDownType.LEFT -> weaponCoolRegistry.get(group).leftCD = time
            }
        } catch (e: Exception) {
            RendogClient.LOG.error("Exception while updating the weapon cooldown. WeaponGroup: $group")
            e.printStackTrace()
        }
    }

    private fun checkVillageValidation(player: PlayerEntity, weaponName: String): Boolean {
        return ((player.world.spawnPos != BlockPos(0, 0, 0)) ||
                ((player.world.spawnPos == BlockPos(0, 0, 0)) && weaponDataService.isAbleInVillage(weaponName)))
    }
}