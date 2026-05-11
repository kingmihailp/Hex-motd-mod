package com.kingmihailp.hexmotd.event;

import com.kingmihailp.hexmotd.MotdCache;
import com.kingmihailp.hexmotd.config.HexMotdConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.nio.file.Path;

public class ServerPingHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    /** Path to the config file on disk; captured on first load. */
    static Path configPath;

    // ── Mod-bus listeners ────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == HexMotdConfig.SPEC) {
            configPath = event.getConfig().getFullPath();
            MotdCache.updateFromSpec();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == HexMotdConfig.SPEC) {
            configPath = event.getConfig().getFullPath();
            MotdCache.updateFromSpec();
            LOGGER.info("[HexMOTD] Config reloaded (file watcher).");
        }
    }

    // ── Called by /hexmotd reload ─────────────────────────────────────────────

    /**
     * Re-reads the TOML file from disk immediately and updates {@link MotdCache}.
     * Does not depend on any internal NeoForge reload API.
     */
    public static boolean forceReload() {
        if (configPath == null) {
            LOGGER.warn("[HexMOTD] Config path unknown – server config may not be loaded yet.");
            return false;
        }
        return MotdCache.reloadFromDisk(configPath);
    }
}
