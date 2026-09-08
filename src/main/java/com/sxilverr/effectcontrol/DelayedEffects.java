package com.sxilverr.effectcontrol;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class DelayedEffects {
    private static final List<Pending> QUEUE = new ArrayList<>();

    private DelayedEffects() {
    }

    public static void queue(UUID player, List<ResourceLocation> removals, List<MobEffectInstance> grants, int delay) {
        QUEUE.add(new Pending(player, removals, grants, delay));
    }

    public static void tick(MinecraftServer server) {
        if (QUEUE.isEmpty()) {
            return;
        }
        Iterator<Pending> iterator = QUEUE.iterator();
        while (iterator.hasNext()) {
            Pending pending = iterator.next();
            if (--pending.remaining > 0) {
                continue;
            }
            iterator.remove();
            ServerPlayer player = server.getPlayerList().getPlayer(pending.player);
            if (player == null) {
                continue;
            }
            EffectApplier.run(player, pending.removals, pending.grants);
        }
    }

    public static void clear() {
        QUEUE.clear();
    }

    private static final class Pending {
        private final UUID player;
        private final List<ResourceLocation> removals;
        private final List<MobEffectInstance> grants;
        private int remaining;

        private Pending(UUID player, List<ResourceLocation> removals, List<MobEffectInstance> grants, int remaining) {
            this.player = player;
            this.removals = removals;
            this.grants = grants;
            this.remaining = remaining;
        }
    }
}
