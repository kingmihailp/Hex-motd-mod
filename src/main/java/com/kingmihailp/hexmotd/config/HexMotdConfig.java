package com.kingmihailp.hexmotd.config;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class HexMotdConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.ConfigValue<String> MOTD_LINE1;
    public static final ModConfigSpec.ConfigValue<String> MOTD_LINE2;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment(
            "=== HexMOTD Configuration ===",
            "Hex color format  : &#RRGGBB  (e.g. &#FF5500)",
            "Standard colors   : &0-9, &a-f",
            "Formatting codes  : &l=bold, &o=italic, &n=underline, &m=strikethrough, &k=obfuscated, &r=reset"
        );

        ENABLED = BUILDER
            .comment("Enable hex-color MOTD replacement.")
            .define("enabled", true);

        MOTD_LINE1 = BUILDER
            .comment("First line of MOTD. Leave empty to keep the server.properties value.")
            .define("motd_line1", "&#FF5500&lWelcome &#FFFF00to my server&r!");

        MOTD_LINE2 = BUILDER
            .comment("Second line of MOTD. Leave empty to omit the second line.")
            .define("motd_line2", "&#00FF00Play and have fun!");

        SPEC = BUILDER.build();
    }

    private HexMotdConfig() {}
}
