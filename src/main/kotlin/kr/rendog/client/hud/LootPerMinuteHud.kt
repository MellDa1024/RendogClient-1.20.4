package kr.rendog.client.hud

import kr.rendog.client.config.Config
import kr.rendog.client.service.LootPerMinuteService
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import kotlin.jvm.optionals.getOrNull
import kotlin.math.roundToInt

class LootPerMinuteHud(
    private val lootPerMinuteService: LootPerMinuteService
): HudRenderCallback {

    private val mc = MinecraftClient.getInstance()

    override fun onHudRender(drawContext: DrawContext, tickDelta: Float) {
        if (!Config.lootPerMinuteEnabled) return
        val matrices = drawContext.matrices

        val baseX = (mc.window.scaledWidth / 2.0f).roundToInt().toFloat()
        val baseY = (mc.window.scaledHeight / 2.0f).roundToInt().toFloat()

        val x = if (Config.lootPerMinuteGUIX != 0.0) baseX * (1 + Config.lootPerMinuteGUIX.toFloat())
        else baseX
        val y = if (Config.lootPerMinuteGUIY != 0.0) baseY * (1 + Config.lootPerMinuteGUIY.toFloat())
        else baseY

        val scale = Config.lootPerMinuteGUIScale.toFloat()
        matrices.push()
        matrices.translate(x, y, 0.0f)
        matrices.scale(scale, scale, scale)

        val lpm = lootPerMinuteService.getLPM()
        val startColor = TextColor.parse(Config.lootPerMinuteGUIStartColor).result().getOrNull()
        val endColor = TextColor.parse(Config.lootPerMinuteGUIEndColor).result().getOrNull()
        val color = if (startColor == null || endColor == null) {
            16777215
        } else {
            getColor(startColor.rgb, endColor.rgb, (lpm / 250.0).coerceIn(0.0, 1.0))
        }
        val text = Text.literal(Config.lootPerMinuteGUIPrefix).append(Text.literal(" $lpm").withColor(color))
        drawContext.drawText(mc.textRenderer, text, -mc.textRenderer.getWidth("${Config.lootPerMinuteGUIPrefix} ") - mc.textRenderer.getWidth("$lpm") / 2, 0, 16777215, Config.lootPerMinuteGUITextShadow)
        matrices.pop()
    }

    private fun getColor(startColor: Int, endColor: Int, coefficient: Double): Int {
        val sr = (startColor shr 16) and 0xFF
        val sg = (startColor shr 8) and 0xFF
        val sb = startColor and 0xFF

        val tr = (endColor shr 16) and 0xFF
        val tg = (endColor shr 8) and 0xFF
        val tb = endColor and 0xFF

        val rr = (sr * (1 - coefficient) + tr * coefficient).toInt().coerceIn(0, 255)
        val rg = (sg * (1 - coefficient) + tg * coefficient).toInt().coerceIn(0, 255)
        val rb = (sb * (1 - coefficient) + tb * coefficient).toInt().coerceIn(0, 255)

        return (rr shl 16) or (rg shl 8) or rb
    }
}