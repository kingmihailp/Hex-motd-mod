package com.kingmihailp.hexmotd.mixin;

import com.kingmihailp.hexmotd.HexMotd;
import com.kingmihailp.hexmotd.MotdCache;
import com.kingmihailp.hexmotd.util.ColorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts MinecraftServer.getStatus() – called every time a client pings the server.
 * Replacing the description here is safe because:
 *  - getStatus() is a simple getter present in all Minecraft versions.
 *  - We never touch the packet layer, so no Netty / protocol-phase issues.
 *  - A try-catch(Throwable) means any unexpected API mismatch is logged, not propagated.
 */
@Mixin(MinecraftServer.class)
public class ServerStatusMixin {

    @Inject(
        method  = "getStatus",
        at      = @At("RETURN"),
        cancellable = true,
        require = 0   // optional: if the method is somehow absent, don't crash
    )
    private void hexMotd_onGetStatus(CallbackInfoReturnable<ServerStatus> cir) {
        if (!MotdCache.ready || !MotdCache.enabled) return;

        ServerStatus original = cir.getReturnValue();
        if (original == null) return;

        try {
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

            cir.setReturnValue(new ServerStatus(
                motd,
                original.players(),
                original.version(),
                original.favicon(),
                original.enforcesSecureChat()
            ));
        } catch (Throwable t) {
            HexMotd.LOGGER.error("[HexMOTD] Failed to modify MOTD in getStatus(): {}", t.toString());
        }
    }
}
