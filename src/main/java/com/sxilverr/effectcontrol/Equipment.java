package com.sxilverr.effectcontrol;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public final class Equipment {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private static CurioSlots curios = null;

    private Equipment() {
    }

    public interface CurioSlots {
        boolean isEquipped(ServerPlayer player, Predicate<ItemStack> test);
    }

    public static void setCurioSlots(CurioSlots access) {
        curios = access;
    }

    public static boolean curiosAvailable() {
        return curios != null;
    }

    public static boolean isWearing(ServerPlayer player, List<ResourceLocation> items, CompoundTag nbt) {
        if (items.isEmpty()) {
            return false;
        }
        Predicate<ItemStack> test = stack -> matches(stack, items, nbt);
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (test.test(player.getItemBySlot(slot))) {
                return true;
            }
        }
        return curios != null && curios.isEquipped(player, test);
    }

    private static boolean matches(ItemStack stack, List<ResourceLocation> items, CompoundTag nbt) {
        if (stack.isEmpty() || !items.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()))) {
            return false;
        }
        return nbt == null || ItemNbt.matches(nbt, stack);
    }
}
