package com.sxilverr.effectcontrol.forge;

import com.sxilverr.effectcontrol.Settings;
import com.sxilverr.effectcontrol.EffectControl;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = EffectControl.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EffectControlConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> EFFECTS = BUILDER
            .comment("Effects for join, respawn, and dimension change.")
            .defineList("effects", List.<String>of(), entry -> entry instanceof String);

    private static final ForgeConfigSpec.IntValue APPLY_DELAY_TICKS = BUILDER
            .comment(
                    "Ticks to wait before giving effects.",
                    "Use this if a mod clears effects when a player spawns."
            )
            .defineInRange("apply_delay_ticks", 0, 0, 200);

    private static final ForgeConfigSpec.IntValue SCAN_INTERVAL_TICKS = BUILDER
            .comment(
                    "How often to re-check near and away rules, in ticks."
            )
            .defineInRange("scan_interval_ticks", 20, 1, 200);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private EffectControlConfig() {
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
