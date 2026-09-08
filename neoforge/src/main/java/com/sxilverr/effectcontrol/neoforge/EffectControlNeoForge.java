package com.sxilverr.effectcontrol.neoforge;

import com.sxilverr.effectcontrol.EffectControl;
import com.sxilverr.effectcontrol.Equipment;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

@Mod(EffectControl.MOD_ID)
public final class EffectControlNeoForge {
    public EffectControlNeoForge(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, EffectControlNeoConfig.SPEC);
        if (ModList.get().isLoaded("curios")) {
            Equipment.setCurioSlots(new CuriosCompat());
        }
    }
}
