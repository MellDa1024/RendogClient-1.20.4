package kr.rendog.client.registry

import kr.rendog.client.data.PlayerWeaponCD
import java.util.concurrent.ConcurrentHashMap

class WeaponCoolRegistry {
    private val cooldown = ConcurrentHashMap<String, PlayerWeaponCD>()

    fun register(weaponName: String) {
        val time = System.currentTimeMillis()
        cooldown[weaponName] = PlayerWeaponCD(time, time)
    }

    fun find(weaponName: String): PlayerWeaponCD? {
        return cooldown[weaponName]
    }

    fun get(weaponName: String): PlayerWeaponCD {
        return cooldown[weaponName]!!
    }
}