package kr.rendog.client.hud

import kr.rendog.client.RendogClient
import kr.rendog.client.config.MainConfig
import kr.rendog.client.registry.WeaponCoolRegistry
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import kotlin.math.roundToInt

class CooldownHud (
    private val weaponCoolRegistry: WeaponCoolRegistry,
    private val config: MainConfig
): HudRenderCallback {

    private val mc = MinecraftClient.getInstance()

    override fun onHudRender(drawContext: DrawContext, tickDelta: Float) {
        val player = mc.player ?: return
        val matrices = drawContext.matrices

        val baseX = (mc.window.scaledWidth / 2.0f).roundToInt().toFloat() - 0.5f
        val baseY = (mc.window.scaledHeight / 2.0f).roundToInt().toFloat()

        val x = if (config.cooldownConfig.x != 0) baseX * (1 + config.cooldownConfig.x / 100.0f)
        else baseX
        val y = if (config.cooldownConfig.y != 0) baseY * (1 + config.cooldownConfig.y / 100.0f)
        else baseY

        val scale = config.cooldownConfig.scale / 100.0f
        matrices.push()
        matrices.translate(x, y, 0.0f)
        matrices.translate(-90.0f * scale, 0.0f, 0.0f)
        matrices.scale(scale, scale, scale)
        repeat(9) { slot ->
            drawCooldown(drawContext, player, slot, 2, 2)
            matrices.translate(20.0f, 0.0f, 0.0f)
        }
        matrices.pop()
    }

    private fun drawCooldown(drawContext: DrawContext, player: PlayerEntity, slot: Int, x: Int, y: Int) {
        val weapon = player.inventory.getStack(slot)
        val weaponName = weapon.name.string

        val cd = weaponCoolRegistry.find(weaponName) ?: run {
            drawCooldownItem(drawContext, weapon, x, y, "", "")
            return
        }

        val rightCooldown = ((cd.rightCD - System.currentTimeMillis()).toDouble() / 100.0).roundToInt() / 10.0
        val leftCooldown = ((cd.leftCD - System.currentTimeMillis()).toDouble() / 100.0).roundToInt() / 10.0

        val rightCoolText = if (rightCooldown <= 0) ""
        else if (rightCooldown > 60) "§c${convert2Min(rightCooldown)}"
        else "§c$rightCooldown"

        val leftCoolText = if (leftCooldown <= 0) ""
        else if (leftCooldown > 60) "§e${convert2Min(leftCooldown)}"
        else "§e$leftCooldown"
        drawCooldownItem(drawContext, weapon, x, y, rightCoolText, leftCoolText)
    }

    private fun drawCooldownItem(drawContext: DrawContext, itemStack: ItemStack, x: Int, y: Int, rightCool: String, leftCool: String) {
        drawContext.drawItemWithoutEntity(itemStack, x, y)
        if (leftCool.isBlank() && rightCool.isBlank()) return

        drawContext.matrices.push()
        drawContext.matrices.translate(0.0f, 0.0f, 200.0f)

        if (leftCool.isNotBlank()) drawContext.drawText(mc.textRenderer, leftCool, x, y, 16777215, true)
        if (rightCool.isNotBlank()) drawContext.drawText(mc.textRenderer, rightCool, x + 17 - mc.textRenderer.getWidth(rightCool), y + 9, 16777215, true)

        drawContext.matrices.pop()
    }

    private fun convert2Min(time: Double): String {
        val minute = (time / 60).toInt()
        val second = (time.roundToInt() % 60)
        return if (second < 10) "$minute:0$second"
        else "$minute:$second"
    }
}