package com.sxilverr.effectcontrol;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record TriggerContext(
        EventKind kind,
        ResourceLocation from,
        ResourceLocation to,
        ResourceLocation block,
        ResourceLocation entity,
        ResourceLocation item,
        String source,
        ResourceLocation advancement,
        String ruleKey,
        ItemStack stack
) {
    public static TriggerContext spawn(EventKind kind, ResourceLocation dimension) {
        return new TriggerContext(kind, null, dimension, null, null, null, null, null, null, null);
    }

    public static TriggerContext dimension(ResourceLocation from, ResourceLocation to) {
        return new TriggerContext(EventKind.CHANGE_DIMENSION, from, to, null, null, null, null, null, null, null);
    }

    public static TriggerContext entity(EventKind kind, ResourceLocation dimension, ResourceLocation entity, String source) {
        return new TriggerContext(kind, null, dimension, null, entity, null, source, null, null, null);
    }

    public static TriggerContext item(EventKind kind, ResourceLocation dimension, ItemStack stack, ResourceLocation block) {
        ResourceLocation item = stack == null || stack.isEmpty() ? null
                : BuiltInRegistries.ITEM.getKey(stack.getItem());
        return new TriggerContext(kind, null, dimension, block, null, item, null, null, null, stack);
    }

    public static TriggerContext advancement(ResourceLocation dimension, ResourceLocation advancement) {
        return new TriggerContext(EventKind.ADVANCEMENT, null, dimension, null, null, null, null, advancement, null, null);
    }

    public static TriggerContext state(EventKind kind, ResourceLocation dimension, String ruleKey) {
        return new TriggerContext(kind, null, dimension, null, null, null, null, null, ruleKey, null);
    }
}
