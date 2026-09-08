package com.sxilverr.effectcontrol.neoforge;

import com.sxilverr.effectcontrol.Settings;
import com.sxilverr.effectcontrol.EffectControl;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;

@EventBusSubscriber(modid = EffectControl.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class EffectControlNeoConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.ConfigValue<List<? extends String>> EFFECTS = BUILDER
            .comment("Effects for join, respawn, and dimension change.")
            .defineList("effects", List.<String>of(), () -> "", entry -> entry instanceof String);

    private static final ModConfigSpec.IntValue APPLY_DELAY_TICKS = BUILDER
            .comment(
                    "Ticks to wait before giving effects.",
                    "Use this if a mod clears effects when a player spawns."
            )
            .defineInRange("apply_delay_ticks", 0, 0, 200);

    private static final ModConfigSpec.IntValue SCAN_INTERVAL_TICKS = BUILDER
            .comment(
                    "How often to re-check near and away rules, in ticks."
            )
            .defineInRange("scan_interval_ticks", 20, 1, 200);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private EffectControlNeoConfig() {
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            sync();
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            sync();
        }
    }

    private static void sync() {
        Settings.setRules(EFFECTS.get());
        Settings.setApplyDelayTicks(APPLY_DELAY_TICKS.get());
        Settings.setScanIntervalTicks(SCAN_INTERVAL_TICKS.get());
    }
}
