package kr.rendog.client.service

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kr.rendog.client.RendogClient
import kr.rendog.client.config.Config
import kr.rendog.client.data.CoolDownType
import kr.rendog.client.data.WeaponCDData
import kr.rendog.client.utils.ConnectionUtils
import kr.rendog.client.utils.MessageUtils
import kr.rendog.client.utils.TextUtils.deColorize
import java.util.concurrent.ConcurrentHashMap

class WeaponDataService {
    private companion object {
        private const val URL = "https://raw.githubusercontent.com/MellDa1024/RendogDataBase/main/WeaponDataV3.json"
    }

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private var coolDown = ConcurrentHashMap<String, WeaponCDData>()
    private lateinit var coolDownData: WeaponDataList
    private var enabled = false

    fun inDatabase(item: String): Boolean {
        return if (enabled) {
            coolDown.containsKey(item.deColorize().trim())
        } else {
            MessageUtils.sendErrorMessage("Failed to load CoolDown data.")
            false
        }
    }

    private fun getCD(item: String, cdType: CoolDownType): Double {
        return if (enabled) {
            when (cdType) {
                CoolDownType.RIGHT -> coolDown[item.deColorize()]?.rightCD ?: 0.0
                CoolDownType.LEFT -> coolDown[item.deColorize()]?.leftCD ?: 0.0
            }
        } else {
            MessageUtils.sendErrorMessage("Failed to load CoolDown data.")
            0.0
        }
    }

    fun getFinalWeaponCooldown(weaponName: String, type: CoolDownType): Double {
        val cooldown = getCD(weaponName, type)
        val cdr = Config.cdr
        return if (cdr == 0.0) cooldown
        else cooldown * (1 - cdr / 100.0)
    }

    fun getGroup(item: String): String {
        return if (enabled) {
            coolDown[item.deColorize()]?.cooldownGroup ?: item.deColorize()
        } else {
            MessageUtils.sendErrorMessage("Failed to load CoolDown data.")
            item.deColorize()
        }
    }

    fun isAbleInVillage(item: String): Boolean {
        return coolDown[item.deColorize()]?.inVillage ?: false
    }

    fun loadCoolDownData(): Boolean {
        return try {
            val rawJson = ConnectionUtils.requestRawJsonFrom(URL)
            coolDownData = gson.fromJson(rawJson, object : TypeToken<WeaponDataList>() {}.type)
            loadCoolDownData(coolDownData)
        } catch (e: Exception) {
            RendogClient.LOG.error("Failed loading CoolDownData : ", e)
            false
        }
    }

    private fun loadCoolDownData(coolDownData: WeaponDataList): Boolean {
        coolDown.clear()
        if (!coolDownData.availableVersions.contains(RendogClient.VERSION.lowercase())) {
            RendogClient.LOG.error("Update your RendogClient to new version.")
            return false
        } else {
            coolDownData.weaponList.forEach { weaponData ->
                if (weaponData.maxLevel > 1) {
                    for (lvl in 1 until weaponData.maxLevel + 1) {
                        val modifiedWeaponName =
                            if (weaponData.maxLevel != lvl) weaponData.weaponName + " [ +$lvl ]"
                            else weaponData.weaponName + " [ MAX ]"

                        if (!weaponData.changeByLevel) register(modifiedWeaponName, weaponData, 0)
                        else register(modifiedWeaponName, weaponData, lvl - 1)
                    }
                } else {
                    val modifiedWeaponName = weaponData.weaponName + when {
                        weaponData.maxLevel == -1 -> " [ SPECIAL ]"
                        weaponData.weaponName.contains("< 초월 >") -> " [ MAX ] (거래 불가)"
                        weaponData.maxLevel == 1 -> ""
                        else -> throw Exception("Cannot Recognize the Weapon, WeaponName : ${weaponData.weaponName}")
                    }
                    register(modifiedWeaponName, weaponData, 0)
                }
            }
        }
        enabled = coolDownData.enabled
        RendogClient.LOG.info("CoolDown Data loaded.")
        return true
    }

    private fun register(weaponName: String, weaponData: WeaponData, cdIndex: Int) {
        coolDown[weaponName] = WeaponCDData(
            weaponData.cooldownGroup,
            weaponData.leftCD[cdIndex],
            weaponData.rightCD[cdIndex],
            weaponData.inVillage
        )
        //RendogClient.LOG.info("$weaponName registered. Data : ${coolDown[weaponName]}")
    }

    data class WeaponDataList(
        @SerializedName("Enabled")
        var enabled: Boolean = true,
        @SerializedName("AvailableVersions")
        val availableVersions: Array<String>,
        @SerializedName("WeaponList")
        val weaponList: LinkedHashSet<WeaponData>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as WeaponDataList

            if (enabled != other.enabled) return false
            if (!availableVersions.contentEquals(other.availableVersions)) return false
            if (weaponList != other.weaponList) return false

            return true
        }

        override fun hashCode(): Int {
            var result = enabled.hashCode()
            result = 31 * result + availableVersions.contentHashCode()
            result = 31 * result + weaponList.hashCode()
            return result
        }
    }

    data class WeaponData(
        @SerializedName("WeaponName")
        val weaponName: String,
        @SerializedName("maxLevel")
        val maxLevel: Int,
        @SerializedName("changeByLevel")
        val changeByLevel: Boolean,
        @SerializedName("LeftCoolDown")
        val leftCD: Array<Double>,
        @SerializedName("RightCoolDown")
        val rightCD: Array<Double>,
        @SerializedName("InVillage")
        val inVillage: Boolean,
        @SerializedName("CoolDownGroup")
        val cooldownGroup: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as WeaponData

            if (weaponName != other.weaponName) return false
            if (maxLevel != other.maxLevel) return false
            if (changeByLevel != other.changeByLevel) return false
            if (!leftCD.contentEquals(other.leftCD)) return false
            if (!rightCD.contentEquals(other.rightCD)) return false
            if (inVillage != other.inVillage) return false
            if (cooldownGroup != other.cooldownGroup) return false

            return true
        }

        override fun hashCode(): Int {
            var result = weaponName.hashCode()
            result = 31 * result + maxLevel
            result = 31 * result + changeByLevel.hashCode()
            result = 31 * result + leftCD.contentHashCode()
            result = 31 * result + rightCD.contentHashCode()
            result = 31 * result + inVillage.hashCode()
            result = 31 * result + cooldownGroup.hashCode()
            return result
        }
    }
}