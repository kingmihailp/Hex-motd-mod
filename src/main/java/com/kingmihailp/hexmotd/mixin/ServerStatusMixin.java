package com.kingmihailp.hexmotd.mixin;

import com.kingmihailp.hexmotd.MotdCache;
import com.kingmihailp.hexmotd.util.ColorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Intercepts the ServerStatus that is about to be sent to the pinging client
 * and replaces its description with our hex-color MOTD.
 */
@Mixin(ServerStatusPacketListenerImpl.class)
public class ServerStatusMixin {

    @ModifyArg(
        method = "handleStatusRequest",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/protocol/status/ClientboundStatusResponsePacket;<init>(Lnet/minecraft/network/protocol/status/ServerStatus;)V"
        ),
        index = 0
    )
    private ServerStatus hexMotd_replaceDescription(ServerStatus original) {
        if (original == null || !MotdCache.ready || !MotdCache.enabled) return original;

        String l1 = MotdCache.line1;
        String l2 = MotdCache.line2;

        MutableComponent motd = Component.empty();
        if (!l1.isEmpty()) {
            motd.append(ColorParser.parse(l1));
        }
        if (!l2.isEmpty()) {
            if (!l1.isEmpty()) motd.append(Component.literal("\n"));
            motd.append(ColorParser.parse(l2));
        }

        return new ServerStatus(
            motd,
            original.players(),
            original.version(),
            original.favicon(),
            original.enforcesSecureChat()
        );
    }
}
