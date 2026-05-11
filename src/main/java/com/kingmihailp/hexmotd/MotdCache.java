package com.kingmihailp.hexmotd;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.kingmihailp.hexmotd.config.HexMotdConfig;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.nio.file.Path;

/**
 * Volatile cache of MOTD values read from config.
 * Used by the Mixin so it never touches NeoForge internal reload APIs.
 */
public final class MotdCache {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static volatile boolean enabled = true;
    public static volatile String line1   = "";
    public static volatile String line2   = "";
    public static volatile boolean ready  = false;

    private MotdCache() {}

    /** Sync cache from the already-loaded ModConfigSpec values. */
    public static void updateFromSpec() {
        enabled = HexMotdConfig.ENABLED.get();
        line1   = HexMotdConfig.MOTD_LINE1.get();
        line2   = HexMotdConfig.MOTD_LINE2.get();
        ready   = true;
        LOGGER.info("[HexMOTD] Cache updated – enabled={} line1='{}' line2='{}'", enabled, line1, line2);
    }

    /**
     * Reads the TOML file from disk directly and updates the cache.
     * Used by /hexmotd reload for immediate effect without NeoForge's file watcher.
     */
    public static boolean reloadFromDisk(Path configPath) {
        try (CommentedFileConfig cfg = CommentedFileConfig.builder(configPath).build()) {
            cfg.load();
            enabled = cfg.getOrElse("enabled",     true);
            line1   = cfg.getOrElse("motd_line1",  "");
            line2   = cfg.getOrElse("motd_line2",  "");
            ready   = true;
            LOGGER.info("[HexMOTD] Reloaded from disk – enabled={} line1='{}' line2='{}'", enabled, line1, line2);
            return true;
        } catch (Exception e) {
            LOGGER.error("[HexMOTD] Failed to reload from disk: {}", e.getMessage());
            return false;
        }
    }
}
