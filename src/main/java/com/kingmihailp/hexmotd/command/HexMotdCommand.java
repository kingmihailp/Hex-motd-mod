package com.kingmihailp.hexmotd.command;

import com.kingmihailp.hexmotd.config.HexMotdConfig;
import com.kingmihailp.hexmotd.event.ServerPingHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class HexMotdCommand {

    private HexMotdCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("hexmotd")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("reload")
                    .executes(HexMotdCommand::executeReload)
                )
                .then(Commands.literal("status")
                    .executes(HexMotdCommand::executeStatus)
                )
        );
    }

    private static int executeReload(CommandContext<CommandSourceStack> ctx) {
        boolean ok = ServerPingHandler.forceReload();
        if (ok) {
            ctx.getSource().sendSuccess(
                () -> Component.literal("[HexMOTD] Config reloaded successfully.")
                               .withStyle(ChatFormatting.GREEN),
                false
            );
        } else {
            ctx.getSource().sendFailure(
                Component.literal("[HexMOTD] Failed to reload config – check the server log.")
                         .withStyle(ChatFormatting.RED)
            );
        }
        return ok ? 1 : 0;
    }

    private static int executeStatus(CommandContext<CommandSourceStack> ctx) {
        boolean enabled = HexMotdConfig.ENABLED.get();
        String line1   = HexMotdConfig.MOTD_LINE1.get();
        String line2   = HexMotdConfig.MOTD_LINE2.get();

        ctx.getSource().sendSuccess(() -> Component.literal(
            "[HexMOTD] Status\n" +
            "  enabled : " + enabled + "\n" +
            "  line 1  : " + line1   + "\n" +
            "  line 2  : " + line2
        ).withStyle(ChatFormatting.AQUA), false);

        return 1;
    }
}
