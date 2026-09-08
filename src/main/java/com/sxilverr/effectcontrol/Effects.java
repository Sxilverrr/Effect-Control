package com.sxilverr.effectcontrol;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public final class Effects {
    private Effects() {
    }

    //? if >=1.20.5 {
    /*public static boolean exists(ResourceLocation id) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(id).isPresent();
    }

    public static boolean has(ServerPlayer player, ResourceLocation id) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(id).filter(player::hasEffect).isPresent();
    }

    public static void remove(ServerPlayer player, ResourceLocation id) {
        BuiltInRegistries.MOB_EFFECT.getHolder(id).ifPresent(player::removeEffect);
    }

    public static MobEffectInstance instance(EffectSpec spec) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(spec.effect())
                .map(holder -> new MobEffectInstance(holder, spec.durationTicks(), spec.amplifier(),
                        spec.ambient(), spec.particles(), spec.icon()))
                .orElse(null);
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
    //?}
}
