package com.sxilverr.effectcontrol;

import java.util.ArrayList;
import java.util.List;

public final class Settings {
    private static List<EffectRule> rules = List.of();
    private static List<EffectRule> stateRules = List.of();
    private static int applyDelayTicks = 0;
    private static int scanIntervalTicks = 20;

    private Settings() {
    }

    public static void setRules(List<? extends String> raw) {
        List<EffectRule> parsed = new ArrayList<>();
        List<EffectRule> proximity = new ArrayList<>();
        for (String entry : raw) {
            String text = entry == null ? "" : entry.trim();
            if (text.isEmpty() || text.startsWith("#")) {
                continue;
            }
            EffectRule rule = EffectRule.parse(text);
            if (rule == null) {
                EffectControl.LOGGER.warn("Skipping invalid effect entry: {}", text);
                continue;
            }
            parsed.add(rule);
            if (rule.trigger().isStateTrigger()) {
                proximity.add(rule);
            }
        }
        rules = List.copyOf(parsed);
        stateRules = List.copyOf(proximity);
        EffectApplier.forgetMissingEffects();
        EffectApplier.clearCooldowns();
        StateScanner.clear();
    }

    public static void setApplyDelayTicks(int ticks) {
        applyDelayTicks = Math.max(0, ticks);
    }

    public static void setScanIntervalTicks(int ticks) {
        scanIntervalTicks = Math.max(1, ticks);
    }

    public static List<EffectRule> rules() {
        return rules;
    }

    public static List<EffectRule> stateRules() {
        return stateRules;
    }

    public static boolean hasStateRules() {
        return !stateRules.isEmpty();
    }

    public static int applyDelayTicks() {
        return applyDelayTicks;
    }

    public static int scanIntervalTicks() {
        return scanIntervalTicks;
    }
}
