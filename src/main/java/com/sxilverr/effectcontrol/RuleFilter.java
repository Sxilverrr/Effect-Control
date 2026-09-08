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
        List<String> fluids,
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
        if (!blocks.isEmpty() && !blocks.contains(context.block())) {
            return false;
        }
        if (!entities.isEmpty() && !entities.contains(context.entity())) {
            return false;
        }
        if (!items.isEmpty() && !items.contains(context.item())) {
            return false;
        }
        if (!advancements.isEmpty() && !advancements.contains(context.advancement())) {
            return false;
        }
        return sources.isEmpty() || sources.contains(context.source());
    }
}
