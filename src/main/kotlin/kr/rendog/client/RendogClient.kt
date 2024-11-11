package kr.rendog.client

import kr.rendog.client.config.MainConfig
import kr.rendog.client.handler.LeftClickHandler
import kr.rendog.client.handler.RightClickHandler
import kr.rendog.client.handler.ServerJoinHandler
import kr.rendog.client.handler.TickHandler
import kr.rendog.client.handler.chat.LeftChatHandler
import kr.rendog.client.handler.chat.MoonlightHandler
import kr.rendog.client.handler.chat.RightChatHandler
import kr.rendog.client.hud.CooldownHud
import kr.rendog.client.registry.WeaponCoolRegistry
import kr.rendog.client.service.WeaponCoolService
import kr.rendog.client.service.WeaponDataService
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import me.shedaniel.autoconfig.serializer.PartitioningSerializer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class RendogClient: ClientModInitializer {

    companion object {
        const val MOD_ID = "rendogclient"
        val LOG: Logger = LoggerFactory.getLogger("RendogClient")
    }

    override fun onInitializeClient() {
        WeaponDataService.loadCoolDownData()

        val weaponCoolRegistry = WeaponCoolRegistry()
        val weaponCoolService = WeaponCoolService(weaponCoolRegistry)

        AutoConfig.register(
            MainConfig::class.java,
            PartitioningSerializer.wrap { definition: Config?, configClass: Class<ConfigData> ->
                GsonConfigSerializer(
                    definition,
                    configClass
                )
            }
        )

        val config = AutoConfig.getConfigHolder(MainConfig::class.java).config

        HudRenderCallback.EVENT.register(CooldownHud(weaponCoolRegistry, config))
        ClientPlayConnectionEvents.JOIN.register(ServerJoinHandler())

        UseItemCallback.EVENT.register(RightClickHandler(weaponCoolService))
        ClientPreAttackCallback.EVENT.register(LeftClickHandler(weaponCoolService))

        ClientTickEvents.START_WORLD_TICK.register(TickHandler(weaponCoolService))

        ClientReceiveMessageEvents.GAME.register(RightChatHandler(weaponCoolService))
        ClientReceiveMessageEvents.GAME.register(LeftChatHandler(weaponCoolService, config))
        ClientReceiveMessageEvents.GAME.register(MoonlightHandler(weaponCoolService))
    }
}