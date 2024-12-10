package kr.rendog.client.hud

import kr.rendog.client.config.Config
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.Text
import kotlin.math.roundToInt

class GodModeInfoHud: HudRenderCallback {

    private val mc = MinecraftClient.getInstance()

    override fun onHudRender(drawContext: DrawContext, tickDelta: Float) {
        if (!Config.godModeInfoEnabled) return
        val player = mc.player ?: return
        val matrices = drawContext.matrices

        val baseX = (mc.window.scaledWidth / 2.0f).roundToInt().toFloat()
        val baseY = (mc.window.scaledHeight / 2.0f).roundToInt().toFloat()

        val x = if (Config.godModeInfoGUIX != 0.0) baseX * (1 + Config.godModeInfoGUIX.toFloat())
        else baseX
        val y = if (Config.godModeInfoGUIY != 0.0) baseY * (1 + Config.godModeInfoGUIY.toFloat())
        else baseY

        val scale = Config.godModeInfoGUIScale.toFloat()
        matrices.push()
        matrices.translate(x, y, 0.0f)
        matrices.scale(scale, scale, scale)

        val luckEffect = player.getStatusEffect(StatusEffects.LUCK)
        if (luckEffect != null) {
            val duration = luckEffect.duration
            val texts = Config.godModeInfoText
                .replace("&", "§")
                .replace("%DURATION%", convert(duration, luckEffect.isInfinite))
                .replace("%UNIT%", getUnit(duration, luckEffect.isInfinite))
                .split("\\n").map { Text.literal(it) }
            texts.forEachIndexed { index, text ->
                drawContext.drawText(mc.textRenderer, text, -mc.textRenderer.getWidth(text) / 2, index * 9, 16777215, Config.godModeInfoGUITextShadow)
            }
        }
        matrices.pop()
    }

    private fun convert(duration: Int, isInfinite: Boolean): String {
        if (isInfinite) return "Infinity"
        return when (Config.godModeInfoDurationUnit) {
            Config.Companion.DurationUnitEnum.SECOND -> "${(duration.toDouble() / 2).toInt() / 10.0}"
            Config.Companion.DurationUnitEnum.TICK -> "$duration"
        }
    }

    private fun getUnit(duration: Int, isInfinite: Boolean): String {
        return when (Config.godModeInfoDurationUnit) {
            Config.Companion.DurationUnitEnum.SECOND -> {
                if (isInfinite || duration > 20) "Seconds"
                else "Second"
            }
            Config.Companion.DurationUnitEnum.TICK -> {
                if (isInfinite || duration > 1) "Ticks"
                else "Tick"
            }
        }
    }
}