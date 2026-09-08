package com.sxilverr.effectcontrol;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class EffectControlCommand {
    private EffectControlCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(EffectControl.MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("reset")
                        .executes(context -> reset(
                                context.getSource(),
                                List.of(context.getSource().getPlayerOrException())))
                        .then(Commands.literal("all")
                                .executes(context -> resetAll(context.getSource())))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> reset(
                                        context.getSource(),
                                        EntityArgument.getPlayers(context, "targets"))))));
    }

    private static int resetAll(CommandSourceStack source) {
        EffectControlData.get(source.getServer()).resetAll();
        source.sendSuccess(() -> Component.literal("Reset Effect Control counters for all players"), true);
        return 1;
    }

    private static int reset(CommandSourceStack source, Collection<ServerPlayer> targets) {
        EffectControlData data = EffectControlData.get(source.getServer());
        for (ServerPlayer player : targets) {
            data.reset(player.getUUID());
        }
        String names = targets.stream()
                .map(player -> player.getGameProfile().getName())
                .collect(Collectors.joining(", "));
        source.sendSuccess(() -> Component.literal("Reset Effect Control counters for " + names), true);
        return targets.size();
    }
}
