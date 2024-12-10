package kr.rendog.client.modmenu

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import eu.midnightdust.lib.config.MidnightConfig
import kr.rendog.client.RendogClient.Companion.MOD_ID
import net.minecraft.client.gui.screen.Screen

class SettingMenu: ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent: Screen? ->
            MidnightConfig.getScreen(parent, MOD_ID)
        }
    }
}