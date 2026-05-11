package com.kingmihailp.hexmotd.command;

import com.kingmihailp.hexmotd.MotdCache;
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
        ctx.getSource().sendSuccess(() -> Component.literal(
            "[HexMOTD] Current cache\n" +
            "  enabled : " + MotdCache.enabled + "\n" +
            "  line 1  : " + MotdCache.line1   + "\n" +
            "  line 2  : " + MotdCache.line2
        ).withStyle(ChatFormatting.AQUA), false);
        return 1;
    }
}
