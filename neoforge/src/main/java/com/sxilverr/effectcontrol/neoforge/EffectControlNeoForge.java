package com.sxilverr.effectcontrol.neoforge;

import com.sxilverr.effectcontrol.EffectControl;
import com.sxilverr.effectcontrol.Equipment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

@Mod(EffectControl.MOD_ID)
public final class EffectControlNeoForge {
    public EffectControlNeoForge(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, EffectControlNeoConfig.SPEC);
        modBus.addListener(ModConfigEvent.Loading.class, EffectControlNeoConfig::onLoad);
        modBus.addListener(ModConfigEvent.Reloading.class, EffectControlNeoConfig::onReload);
        if (ModList.get().isLoaded("curios")) {
            Equipment.setCurioSlots(new CuriosCompat());
        }
    }
}
