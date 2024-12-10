package kr.rendog.client.hud

import kr.rendog.client.config.Config
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.util.math.MathHelper
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.roundToInt

class PlayerModelHud : HudRenderCallback {

    private val mc = MinecraftClient.getInstance()

    override fun onHudRender(drawContext: DrawContext, tickDelta: Float) {
        if (!Config.playerModelEnabled) return
        val player = mc.player ?: return

        val scaledWidth = mc.window.scaledWidth
        val scaledHeight = mc.window.scaledHeight
        val baseX = (scaledWidth / 2.0f).roundToInt()
        val baseY = (scaledHeight / 2.0f).roundToInt()

        val x = if (Config.playerModelGUIX != 0.0) baseX * (1 + Config.playerModelGUIX.toFloat())
        else baseX.toFloat()
        val y = if (Config.playerModelGUIY != 0.0) baseY * (1 + Config.playerModelGUIY.toFloat())
        else baseY.toFloat()

        val scale = Config.playerModelGUIScale.toFloat()

        val yaw = if (Config.playerModelEmulateYaw) interpolateAndWrap(player.prevYaw, player.yaw) else 0.0f
        val pitch = if (Config.playerModelEmulatePitch) player.pitch else 0.0f


        //Why the fuq they added Quaternionf2???? and also Scissors???????
        val atanYaw = atan(-yaw / 40.0f)
        val atanPitch = atan(-pitch / 40.0f)

        val backupBodyYaw = player.bodyYaw
        val backupYaw = player.yaw
        val backupPitch = player.pitch
        val backupPrevHeadYaw = player.prevHeadYaw
        val backupHeadYaw = player.headYaw

        player.bodyYaw = 180.0f + atanYaw * 20.0f
        player.yaw = 180.0f + atanYaw * 40.0f
        player.pitch = -atanPitch * 20.0f
        player.headYaw = player.yaw
        player.prevHeadYaw = player.yaw

        InventoryScreen.drawEntity(drawContext, x, y, (30 * scale).toInt(), Vector3f(), Quaternionf().rotateZ(PI.toFloat()), null, player)

        player.bodyYaw = backupBodyYaw
        player.yaw = backupYaw
        player.pitch =  backupPitch
        player.prevHeadYaw = backupPrevHeadYaw
        player.headYaw = backupHeadYaw
    }

    private fun interpolateAndWrap(prev: Float, current: Float): Float {
        return MathHelper.wrapDegrees(prev + (current - prev) * (if (mc.isPaused) mc.pausedTickDelta else mc.tickDelta) + 180)
    }
}