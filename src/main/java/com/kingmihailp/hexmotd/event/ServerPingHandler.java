package com.kingmihailp.hexmotd.event;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.kingmihailp.hexmotd.HexMotd;
import com.kingmihailp.hexmotd.config.HexMotdConfig;
import com.kingmihailp.hexmotd.util.ColorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.server.ServerListPingEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.nio.file.Path;

public class ServerPingHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    /** Path to the loaded server config file; set on first load. */
    private static Path configPath;

    /** Called on the MOD event bus when our server config is loaded or reloaded. */
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        captureConfigPath(event.getConfig());
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        captureConfigPath(event.getConfig());
        LOGGER.info("[HexMOTD] Config reloaded from disk.");
    }

    private static void captureConfigPath(ModConfig config) {
        if (config.getSpec() == HexMotdConfig.SPEC) {
            configPath = config.getFullPath();
        }
    }

    /** Called on the NEOFORGE event bus when a client pings the server. */
    @SubscribeEvent
    public static void onServerListPing(ServerListPingEvent event) {
        if (!HexMotdConfig.ENABLED.get()) return;

        String line1 = HexMotdConfig.MOTD_LINE1.get();
        String line2 = HexMotdConfig.MOTD_LINE2.get();

        MutableComponent motd = Component.empty();
        if (!line1.isEmpty()) {
            motd.append(ColorParser.parse(line1));
        }
        if (!line2.isEmpty()) {
            if (!line1.isEmpty()) motd.append(Component.literal("\n"));
            motd.append(ColorParser.parse(line2));
        }

        event.setMotd(motd);
    }

    /**
     * Forces an immediate reload of the server config from disk.
     * Used by the /hexmotd reload command.
     */
    public static boolean forceReload() {
        if (configPath == null) {
            LOGGER.warn("[HexMOTD] Config path not yet captured – cannot force reload.");
            return false;
        }
        try {
            CommentedFileConfig fileConfig = CommentedFileConfig.builder(configPath)
                .sync()
                .build();
            fileConfig.load();
            fileConfig.close();
            HexMotdConfig.SPEC.acceptConfig(fileConfig);
            LOGGER.info("[HexMOTD] Config force-reloaded from {}.", configPath);
            return true;
        } catch (Exception e) {
            LOGGER.error("[HexMOTD] Failed to reload config: {}", e.getMessage());
            return false;
        }
    }
}
