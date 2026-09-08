package com.sxilverr.effectcontrol;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;

public final class ItemNbt {
    private ItemNbt() {
    }

    //? if >=1.20.5 {
    /*public static boolean matches(CompoundTag expected, ItemStack stack) {
        net.minecraft.world.item.component.CustomData data =
                stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return data != null && NbtUtils.compareNbt(expected, data.copyTag(), true);
    }
    *///?} else {
    public static boolean matches(CompoundTag expected, ItemStack stack) {
        return stack.getTag() != null && NbtUtils.compareNbt(expected, stack.getTag(), true);
    }
    //?}
}
