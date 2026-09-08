package com.sxilverr.effectcontrol;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EffectApplier {
    private static final Set<ResourceLocation> MISSING_EFFECTS = ConcurrentHashMap.newKeySet();
    private static final Map<String, Long> COOLDOWNS = new ConcurrentHashMap<>();

    private EffectApplier() {
    }

    public static void forgetMissingEffects() {
        MISSING_EFFECTS.clear();
    }

    public static void apply(ServerPlayer player, TriggerContext context) {
        List<EffectRule> rules = Settings.rules();
        if (rules.isEmpty()) {
            return;
        }

        EffectControlData data = EffectControlData.get(player.server);
        UUID id = player.getUUID();
        List<ResourceLocation> removals = new ArrayList<>();
        List<MobEffectInstance> grants = new ArrayList<>();

        for (EffectRule rule : rules) {
            if (!rule.matches(context)) {
                continue;
            }
            if (!Environment.matches(player, rule.world())) {
                continue;
            }
            if (!conditionsMet(player, rule)) {
                continue;
            }
            if (!flagsMet(data, id, rule)) {
                continue;
            }
            if (!offCooldown(player, id, rule)) {
                continue;
            }
            if (rule.limit() > 0 && data.count(id, rule.key()) >= rule.limit()) {
                continue;
            }
            if (rule.effect() != null) {
                if (!known(rule.effect().effect())) {
                    continue;
                }
                if (rule.action() == EffectAction.REMOVE) {
                    removals.add(rule.effect().effect());
                } else {
                    MobEffectInstance instance = Effects.instance(rule.effect());
                    if (instance == null) {
                        continue;
                    }
                    grants.add(instance);
                }
            }
            if (rule.limit() > 0) {
                data.increment(id, rule.key());
            }
            applyFlags(data, id, rule);
        }

        if (removals.isEmpty() && grants.isEmpty()) {
            return;
        }

        int delay = Settings.applyDelayTicks();
        if (delay > 0) {
            DelayedEffects.queue(id, removals, grants, delay);
            return;
        }
        run(player, removals, grants);
    }

    public static void clearCooldowns() {
        COOLDOWNS.clear();
    }

    private static boolean offCooldown(ServerPlayer player, UUID id, EffectRule rule) {
        int cooldown = rule.world().cooldownTicks();
        if (cooldown <= 0) {
            return true;
        }
        String key = id + "|" + rule.key();
        long now = player.serverLevel().getGameTime();
        Long last = COOLDOWNS.get(key);
        if (last != null && now - last < cooldown) {
            return false;
        }
        COOLDOWNS.put(key, now);
        return true;
    }

    private static boolean flagsMet(EffectControlData data, UUID player, EffectRule rule) {
        for (String flag : rule.flags().ifSet()) {
            if (!data.hasFlag(player, flag)) {
                return false;
            }
        }
        for (String flag : rule.flags().ifUnset()) {
            if (data.hasFlag(player, flag)) {
                return false;
            }
        }
        return true;
    }

    private static void applyFlags(EffectControlData data, UUID player, EffectRule rule) {
        for (String flag : rule.flags().set()) {
            data.setFlag(player, flag);
        }
        for (String flag : rule.flags().unset()) {
            data.clearFlag(player, flag);
        }
    }

    private static boolean conditionsMet(ServerPlayer player, EffectRule rule) {
        for (ResourceLocation id : rule.requires()) {
            if (!known(id) || !Effects.has(player, id)) {
                return false;
            }
        }
        for (ResourceLocation id : rule.missing()) {
            if (known(id) && Effects.has(player, id)) {
                return false;
            }
        }
        return true;
    }

    private static boolean known(ResourceLocation id) {
        if (Effects.exists(id)) {
            return true;
        }
        if (MISSING_EFFECTS.add(id)) {
            EffectControl.LOGGER.warn("No such effect: {}", id);
        }
        return false;
    }

    static void run(ServerPlayer player, List<ResourceLocation> removals, List<MobEffectInstance> grants) {
        for (ResourceLocation effect : removals) {
            Effects.remove(player, effect);
        }
        for (MobEffectInstance effect : grants) {
            player.addEffect(new MobEffectInstance(effect));
        }
    }
}
