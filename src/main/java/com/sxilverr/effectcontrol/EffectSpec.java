package com.sxilverr.effectcontrol;

import net.minecraft.resources.ResourceLocation;

public record EffectSpec(
        ResourceLocation effect,
        int durationTicks,
        int amplifier,
        boolean ambient,
        boolean particles,
        boolean icon
) {
}
