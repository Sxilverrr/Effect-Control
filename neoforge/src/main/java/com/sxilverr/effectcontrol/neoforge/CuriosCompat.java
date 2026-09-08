package com.sxilverr.effectcontrol.neoforge;

import com.sxilverr.effectcontrol.Equipment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Predicate;

public final class CuriosCompat implements Equipment.CurioSlots {
    @Override
    public boolean isEquipped(ServerPlayer player, Predicate<ItemStack> test) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.findFirstCurio(test))
                .isPresent();
    }
}
