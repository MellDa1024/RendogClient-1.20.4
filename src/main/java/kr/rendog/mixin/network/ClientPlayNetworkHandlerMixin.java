package kr.rendog.mixin.network;

import kr.rendog.client.config.Config;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Redirect(
            method = "onPlayerList",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V")
    )
    private void suppressPlayerListWarning(Logger logger, String msg, Object o1, Object o2) {
        // Ignoring player info update for unknown player
        if (!Config.suppressPacketWarnings) {
            logger.warn(msg, o1, o2);
        }
    }

    @Redirect(
            method = "onEntityPassengersSet",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V")
    )
    private void suppressPassengersWarning(Logger logger, String msg) {
        // Received passengers for unknown entity
        if (!Config.suppressPacketWarnings) {
            logger.warn(msg);
        }
    }

    @Redirect(
            method = "createEntity",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V")
    )
    private void suppressCreateEntityWarning(Logger logger, String msg, Object o) {
        // Server attempted to add player prior to sending player info
        if (!Config.suppressPacketWarnings) {
            logger.warn(msg, o);
        }
    }

    @Redirect(
            method = "onEntitySpawn",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V")
    )
    private void suppressEntitySpawnWarning(Logger logger, String msg, Object o) {
        // Skipping Entity with id
        if (!Config.suppressPacketWarnings) {
            logger.warn(msg, o);
        }
    }
}
