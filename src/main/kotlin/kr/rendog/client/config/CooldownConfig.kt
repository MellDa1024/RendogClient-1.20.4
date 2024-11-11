package kr.rendog.client.config

import kr.rendog.client.RendogClient.Companion.MOD_ID
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip


@Config(name = MOD_ID + "_cooldown")
class CooldownConfig : ConfigData {
    @BoundedDiscrete(min = -100, max = 100)
    @Tooltip
    var x = 0

    @BoundedDiscrete(min = -100, max = 100)
    @Tooltip
    var y = 0

    @BoundedDiscrete(min = 100, max = 200)
    @Tooltip
    var scale = 100

    @BoundedDiscrete(min = 100, max = 1000)
    @Tooltip(count = 2)
    var chatDelay = 150
}