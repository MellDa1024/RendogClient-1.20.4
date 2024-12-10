package kr.rendog.client.config

import eu.midnightdust.lib.config.MidnightConfig

class Config : MidnightConfig() {
    companion object {
        private const val COOLDOWN = "cooldown"
        private const val HEALTH = "health"

        //Cooldown
        @JvmField
        @Entry(category = COOLDOWN)
        var cooldownEnabled = true

        @JvmField
        @Entry(category = COOLDOWN, isSlider = true, min = -1.0, max = 1.0, precision = 100)
        var cooldownGUIX = 0.0

        @JvmField
        @Entry(category = COOLDOWN, isSlider = true, min = -1.0, max = 1.0, precision = 100)
        var cooldownGUIY = 0.0

        @JvmField
        @Entry(category = COOLDOWN, isSlider = true, min = 1.0, max = 3.0, precision = 100)
        var cooldownGUIScale = 1.0

        @JvmField
        @Entry(category = COOLDOWN, isSlider = true, min = 100.0, max = 1000.0, precision = 10)
        var cooldownChatDelay = 150.0

        @JvmField
        @Entry(category = COOLDOWN, min = 0.0, max = 100.0)
        var cdr = 0.0

        @JvmField
        @Entry(category = COOLDOWN)
        var cooldownGUIRenderOnlyText = false

        //Health
        @JvmField
        @Entry(category = HEALTH)
        var healthEnabled = true

        @JvmField
        @Entry(category = HEALTH, isSlider = true, min = -1.0, max = 1.0, precision = 100)
        var healthGUIX = 0.0

        @JvmField
        @Entry(category = HEALTH, isSlider = true, min = -1.0, max = 1.0, precision = 100)
        var healthGUIY = 0.0

        @JvmField
        @Entry(category = HEALTH, isSlider = true, min = 1.0, max = 3.0, precision = 100)
        var healthGUIScale = 1.0

        @JvmField
        @Entry(category = HEALTH, min = 1.0, max = 9.223372036854776E18)
        var maxHealth = 20.0

        @JvmField
        @Entry(category = HEALTH, min = 1.0, max = 9.223372036854776E18)
        var lowHealthThreshold = 20.0

        @JvmField
        @Entry(category = HEALTH)
        var healthGUIPrefix = "Health:"

        @JvmField
        @Entry(category = HEALTH, width = 7, isColor = true)
        var healthGUINormalColor = "#FFFFFF"

        @JvmField
        @Entry(category = HEALTH, width = 7, isColor = true)
        var healthGUILowHealthColor = "#990000"

        @JvmField
        @Entry(category = HEALTH, width = 7, isColor = true)
        var healthGUIFullHealthColor = "#F29900"
    }
}