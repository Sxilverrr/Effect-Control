package com.sxilverr.effectcontrol.forge;

import com.sxilverr.effectcontrol.EffectControl;
import com.sxilverr.effectcontrol.Equipment;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EffectControl.MOD_ID)
public final class EffectControlForge {
    public EffectControlForge(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, EffectControlConfig.SPEC);
        if (ModList.get().isLoaded("curios")) {
            Equipment.setCurioSlots(new CuriosCompat());
        }
    }
}
