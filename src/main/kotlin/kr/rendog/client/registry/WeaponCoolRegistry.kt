package kr.rendog.client.registry

import kr.rendog.client.data.PlayerWeaponCD
import java.util.concurrent.ConcurrentHashMap

class WeaponCoolRegistry {
    private val cooldown = ConcurrentHashMap<String, PlayerWeaponCD>()

    fun register(group: String) {
        cooldown[group] = PlayerWeaponCD(0L, 0L)
    }

    fun find(group: String): PlayerWeaponCD? {
        return cooldown[group]
    }

    fun get(group: String): PlayerWeaponCD {
        return cooldown[group]!!
    }
}