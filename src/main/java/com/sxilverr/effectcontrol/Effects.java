package com.sxilverr.effectcontrol;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public final class Effects {
    private Effects() {
    }

    //? if >=1.21.2 {
    /*private static java.util.Optional<net.minecraft.core.Holder.Reference<MobEffect>> holder(ResourceLocation id) {
        return BuiltInRegistries.MOB_EFFECT.get(id);
    }
    *///?} else if >=1.20.5 {
    /*private static java.util.Optional<net.minecraft.core.Holder.Reference<MobEffect>> holder(ResourceLocation id) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(id);
    }
    *///?}

    //? if >=1.20.5 {
    /*public static boolean exists(ResourceLocation id) {
        return holder(id).isPresent();
    }

    public static boolean has(ServerPlayer player, ResourceLocation id) {
        return holder(id).filter(player::hasEffect).isPresent();
    }

    public static void remove(ServerPlayer player, ResourceLocation id) {
        holder(id).ifPresent(player::removeEffect);
    }

    public static MobEffectInstance instance(EffectSpec spec) {
        return holder(spec.effect())
                .map(holder -> new MobEffectInstance(holder, spec.durationTicks(), spec.amplifier(),
                        spec.ambient(), spec.particles(), spec.icon()))
                .orElse(null);
    }

    public static ResourceLocation id(MobEffectInstance instance) {
        return BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect().value());
    }
    *///?} else {
    public static boolean exists(ResourceLocation id) {
        return BuiltInRegistries.MOB_EFFECT.get(id) != null;
    }

    public static boolean has(ServerPlayer player, ResourceLocation id) {
        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(id);
        return effect != null && player.hasEffect(effect);
    }

    public static void remove(ServerPlayer player, ResourceLocation id) {
        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(id);
        if (effect != null) {
            player.removeEffect(effect);
        }
    }

    public static MobEffectInstance instance(EffectSpec spec) {
        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(spec.effect());
        if (effect == null) {
            return null;
        }
        return new MobEffectInstance(effect, spec.durationTicks(), spec.amplifier(),
                spec.ambient(), spec.particles(), spec.icon());
    }

    public static ResourceLocation id(MobEffectInstance instance) {
        return BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect());
    }
    //?}
}
