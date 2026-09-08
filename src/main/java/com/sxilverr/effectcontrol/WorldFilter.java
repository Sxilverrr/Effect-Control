package com.sxilverr.effectcontrol;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record WorldFilter(
        Range x,
        Range y,
        Range z,
        Range time,
        Range days,
        Range health,
        Range xpLevel,
        Range light,
        List<String> weather,
        List<ResourceLocation> biomes,
        List<String> tags,
        List<String> gamemodes,
        List<String> difficulties,
        List<ResourceLocation> wearing,
        String statType,
        ResourceLocation statId,
        Range statCount,
        int chance,
        int cooldownTicks
) {
    public static final WorldFilter NONE = new WorldFilter(
            Range.ANY, Range.ANY, Range.ANY, Range.ANY, Range.ANY, Range.ANY, Range.ANY, Range.ANY,
            List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
            null, null, new Range(1, Integer.MAX_VALUE), 100, 0);

    public boolean wantsCoords() {
        return x.bounded() || y.bounded() || z.bounded();
    }
}
