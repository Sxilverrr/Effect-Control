package com.sxilverr.effectcontrol;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

public final class ItemNbt {
    private ItemNbt() {
    }

    public static CompoundTag parse(String value) {
        try {
            //? if >=1.21.5 {
            /*return TagParser.parseCompoundFully(value);
            *///?} else {
            return TagParser.parseTag(value);
            //?}
        } catch (CommandSyntaxException e) {
            return null;
        }
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
