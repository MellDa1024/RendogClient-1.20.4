package kr.rendog.client.hud

import kr.rendog.client.config.Config
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import kotlin.jvm.optionals.getOrNull
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class HealthHud: HudRenderCallback {

    private val mc = MinecraftClient.getInstance()

    override fun onHudRender(drawContext: DrawContext, tickDelta: Float) {
        if (!Config.healthEnabled) return
        val player = mc.player ?: return
        val matrices = drawContext.matrices

        val baseX = (mc.window.scaledWidth / 2.0f).roundToInt().toFloat() - 0.5f
        val baseY = (mc.window.scaledHeight / 2.0f).roundToInt().toFloat()

        val x = if (Config.healthGUIX != 0.0) baseX * (1 + Config.healthGUIX.toFloat())
        else baseX
        val y = if (Config.healthGUIY != 0.0) baseY * (1 + Config.healthGUIY.toFloat())
        else baseY

        val scale = Config.healthGUIScale.toFloat()
        matrices.push()
        matrices.translate(x, y, 0.0f)
        matrices.scale(scale, scale, scale)

        val health = if (player.health == 20f) Config.maxHealth
        else Config.maxHealth / 20 * player.health

        val color = if (health == Config.maxHealth) TextColor.parse(Config.healthGUIFullHealthColor).result().getOrNull() ?: TextColor.fromRgb(15898880)
        else if (health > Config.lowHealthThreshold) TextColor.parse(Config.healthGUINormalColor).result().getOrNull() ?: TextColor.fromRgb(16777215)
        else TextColor.parse(Config.healthGUILowHealthColor).result().getOrNull() ?: TextColor.fromRgb(10027008)

        val text = Text.literal(Config.healthGUIPrefix).append(Text.literal(" ${health.roundToLong()}").withColor(color.rgb))
        drawContext.drawText(mc.textRenderer, text, -mc.textRenderer.getWidth("${Config.healthGUIPrefix} "), 0, 16777215, true)
        matrices.pop()
    }
}