package kr.rendog.client.config

import kr.rendog.client.RendogClient.Companion.MOD_ID
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.Config.Gui.Background
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip
import me.shedaniel.autoconfig.serializer.PartitioningSerializer


@Config(name = MOD_ID)
@Background(Background.TRANSPARENT)
class MainConfig : PartitioningSerializer.GlobalData() {
    @Category(MOD_ID + "_cooldown")
    @CollapsibleObject(startExpanded = true)
    @Tooltip
    val cooldownConfig = CooldownConfig()
}