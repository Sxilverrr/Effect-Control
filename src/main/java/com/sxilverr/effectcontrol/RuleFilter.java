package com.sxilverr.effectcontrol;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record RuleFilter(
        ResourceLocation dimension,
        List<ResourceLocation> blocks,
        List<ResourceLocation> entities,
        List<ResourceLocation> items,
        List<String> sources,
        List<ResourceLocation> advancements,
        List<ResourceLocation> causes,
        List<String> fluids,
        List<ResourceLocation> biomes,
        CompoundTag nbt,
        int radius
) {
    public boolean wantsBlocks() {
        return !blocks.isEmpty();
    }

    public boolean wantsEntities() {
        return !entities.isEmpty();
    }

    public boolean matchesNbt(ItemStack stack) {
        if (nbt == null) {
            return true;
        }
        return stack != null && ItemNbt.matches(nbt, stack);
    }

    public boolean matches(TriggerContext context, Trigger trigger) {
        if (dimension != null) {
            ResourceLocation target = trigger == Trigger.LEAVE ? context.from() : context.to();
            if (!dimension.equals(target)) {
                return false;
            }
        }
        if (trigger.isStateTrigger()) {
            return true;
        }
        if (!matchesNbt(context.stack())) {
            return false;
        }
        return accepts(blocks, context.block())
                && accepts(entities, context.entity())
                && accepts(items, context.item())
                && accepts(advancements, context.advancement())
                && accepts(causes, context.cause())
                && accepts(sources, context.source());
    }

    private static <T> boolean accepts(List<T> wanted, T actual) {
        return wanted.isEmpty() || (actual != null && wanted.contains(actual));
    }
}
