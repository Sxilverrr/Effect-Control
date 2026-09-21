package com.sxilverr.effectcontrol;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class EffectChanges {
    private static Map<Change, Boolean> pending = new LinkedHashMap<>();

    private EffectChanges() {
    }

    public static void note(ServerPlayer player, MobEffectInstance effect, boolean had) {
        if (effect == null || !Settings.hasEffectRules()) {
            return;
        }
        ResourceLocation id = Effects.id(effect);
        if (id != null) {
            pending.putIfAbsent(new Change(player.getUUID(), id), had);
        }
    }

    public static void tick(MinecraftServer server) {
        if (pending.isEmpty()) {
            return;
        }
        Map<Change, Boolean> batch = pending;
        pending = new LinkedHashMap<>();
        for (Map.Entry<Change, Boolean> entry : batch.entrySet()) {
            Change change = entry.getKey();
            ServerPlayer player = server.getPlayerList().getPlayer(change.player());
            if (player == null) {
                continue;
            }
            boolean has = Effects.has(player, change.effect());
            if (has == entry.getValue()) {
                continue;
            }
            EffectApplier.apply(player, TriggerContext.effect(
                    has ? EventKind.EFFECT_GAINED : EventKind.EFFECT_LOST,
                    player.level().dimension().location(), change.effect()));
        }
    }

    public static void clear() {
        pending.clear();
    }

    private record Change(UUID player, ResourceLocation effect) {
    }
}
