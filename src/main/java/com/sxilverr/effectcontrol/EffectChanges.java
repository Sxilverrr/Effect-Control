package com.sxilverr.effectcontrol;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class EffectChanges {
    private static Map<Change, Boolean> pending = new LinkedHashMap<>();
    private static Set<Change> applied = new HashSet<>();

    private EffectChanges() {
    }

    public static void note(ServerPlayer player, MobEffectInstance effect, boolean had) {
        change(player, effect, had);
    }

    public static void added(ServerPlayer player, MobEffectInstance effect, boolean had) {
        Change change = change(player, effect, had);
        if (change != null) {
            applied.add(change);
        }
    }

    private static Change change(ServerPlayer player, MobEffectInstance effect, boolean had) {
        if (effect == null || !Settings.hasEffectRules()) {
            return null;
        }
        ResourceLocation id = Effects.id(effect);
        if (id == null) {
            return null;
        }
        Change change = new Change(player.getUUID(), id);
        pending.putIfAbsent(change, had);
        return change;
    }

    public static void tick(MinecraftServer server) {
        if (pending.isEmpty()) {
            return;
        }
        Map<Change, Boolean> batch = pending;
        Set<Change> added = applied;
        pending = new LinkedHashMap<>();
        applied = new HashSet<>();
        for (Map.Entry<Change, Boolean> entry : batch.entrySet()) {
            Change change = entry.getKey();
            ServerPlayer player = server.getPlayerList().getPlayer(change.player());
            if (player == null) {
                continue;
            }
            boolean has = Effects.has(player, change.effect());
            if (has != entry.getValue()) {
                fire(player, change, has ? EventKind.EFFECT_GAINED : EventKind.EFFECT_LOST);
            }
            if (has && added.contains(change)) {
                fire(player, change, EventKind.EFFECT_APPLIED);
            }
        }
    }

    private static void fire(ServerPlayer player, Change change, EventKind kind) {
        EffectApplier.apply(player, TriggerContext.effect(kind, player.level().dimension().location(), change.effect()));
    }

    public static void clear() {
        pending.clear();
        applied.clear();
    }

    private record Change(UUID player, ResourceLocation effect) {
    }
}
