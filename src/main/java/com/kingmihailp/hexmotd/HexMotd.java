package com.kingmihailp.hexmotd;

import com.kingmihailp.hexmotd.command.HexMotdCommand;
import com.kingmihailp.hexmotd.config.HexMotdConfig;
import com.kingmihailp.hexmotd.event.ServerPingHandler;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;
import net.neoforged.fml.common.Mod;

@Mod(HexMotd.MODID)
public class HexMotd {

    public static final String MODID = "hexmotd";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HexMotd(IEventBus modEventBus, ModContainer modContainer) {
        // Register server config (generates config/hexmotd-server.toml)
        modContainer.registerConfig(ModConfig.Type.SERVER, HexMotdConfig.SPEC);

        // Mod bus: config lifecycle events
        modEventBus.addListener(ServerPingHandler::onConfigLoad);
        modEventBus.addListener(ServerPingHandler::onConfigReload);

        // Game bus: server list ping + command registration
        NeoForge.EVENT_BUS.addListener(ServerPingHandler::onServerListPing);
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
            HexMotdCommand.register(event.getDispatcher())
        );

        LOGGER.info("[HexMOTD] Mod loaded. Config: config/hexmotd-server.toml");
    }
}
