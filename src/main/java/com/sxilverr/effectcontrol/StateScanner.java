package com.sxilverr.effectcontrol;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class StateScanner {
    private static final double DEFAULT_LOOK_DISTANCE = 5.0;

    private static final Map<UUID, Set<String>> ACTIVE = new HashMap<>();
    private static int countdown = 0;

    private StateScanner() {
    }

    public static void clear() {
        ACTIVE.clear();
        countdown = 0;
    }

    public static void forget(UUID player) {
        ACTIVE.remove(player);
    }

    public static void tick(MinecraftServer server) {
        if (!Settings.hasStateRules()) {
            return;
        }
        if (--countdown > 0) {
            return;
        }
        countdown = Settings.scanIntervalTicks();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            scan(player);
        }
    }

    private static void scan(ServerPlayer player) {
        Set<String> previous = ACTIVE.computeIfAbsent(player.getUUID(), id -> new HashSet<>());
        Set<String> current = new HashSet<>();
        ServerLevel level = player.serverLevel();

        for (EffectRule rule : Settings.stateRules()) {
            boolean active = isActive(player, level, rule);
            if (active) {
                current.add(rule.key());
            }
            if (active == previous.contains(rule.key()) || active != rule.trigger().firesOnEnter()) {
                continue;
            }
            EffectApplier.apply(player, TriggerContext.state(
                    rule.trigger().stateKind(), level.dimension().location(), rule.key()));
        }

        previous.clear();
        previous.addAll(current);
    }

    private static boolean isActive(ServerPlayer player, ServerLevel level, EffectRule rule) {
        RuleFilter filter = rule.filter();
        if (filter.dimension() != null && !filter.dimension().equals(level.dimension().location())) {
            return false;
        }
        return switch (rule.trigger().state()) {
            case PROXIMITY -> isNear(player, level, filter);
            case LOOKING -> isLookingAt(player, level, filter);
            case SNEAKING -> player.isShiftKeyDown();
            case SPRINTING -> player.isSprinting();
            case ON_FIRE -> player.isOnFire();
            case IN_FLUID -> isInFluid(player, filter);
            case RIDING -> isRiding(player, filter);
            case SLEEPING -> player.isSleeping();
            case UNDERWATER -> player.isUnderWater();
            default -> false;
        };
    }

    private static boolean isInFluid(ServerPlayer player, RuleFilter filter) {
        for (String fluid : filter.fluids()) {
            if (fluid.equals("water") && player.isInWater()) {
                return true;
            }
            if (fluid.equals("lava") && player.isInLava()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRiding(ServerPlayer player, RuleFilter filter) {
        Entity vehicle = player.getVehicle();
        if (vehicle == null) {
            return false;
        }
        return !filter.wantsEntities()
                || filter.entities().contains(BuiltInRegistries.ENTITY_TYPE.getKey(vehicle.getType()));
    }

    private static boolean isLookingAt(ServerPlayer player, ServerLevel level, RuleFilter filter) {
        double distance = filter.radius() > 0 ? filter.radius() : DEFAULT_LOOK_DISTANCE;
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(distance));

        if (filter.wantsBlocks()) {
            BlockHitResult hit = level.clip(new ClipContext(eye, end,
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            if (hit.getType() == HitResult.Type.BLOCK
                    && filter.blocks().contains(BuiltInRegistries.BLOCK.getKey(
                            level.getBlockState(hit.getBlockPos()).getBlock()))) {
                return true;
            }
        }

        if (filter.wantsEntities()) {
            AABB search = player.getBoundingBox().expandTowards(end.subtract(eye)).inflate(1.0);
            for (Entity entity : level.getEntities(player, search)) {
                if (!filter.entities().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()))) {
                    continue;
                }
                if (entity.getBoundingBox().inflate(0.3).clip(eye, end).isPresent()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isNear(ServerPlayer player, ServerLevel level, RuleFilter filter) {
        int radius = filter.radius();
        if (filter.wantsEntities() && hasEntityNearby(player, level, filter, radius)) {
            return true;
        }
        return filter.wantsBlocks() && hasBlockNearby(player, level, filter, radius);
    }

    private static boolean hasEntityNearby(ServerPlayer player, ServerLevel level, RuleFilter filter, int radius) {
        AABB box = player.getBoundingBox().inflate(radius);
        for (Entity entity : level.getEntities(player, box)) {
            if (entity.distanceTo(player) > radius) {
                continue;
            }
            if (filter.entities().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()))) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasBlockNearby(ServerPlayer player, ServerLevel level, RuleFilter filter, int radius) {
        BlockPos origin = player.blockPosition();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int squared = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > squared) {
                        continue;
                    }
                    cursor.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (!level.isLoaded(cursor)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(cursor);
                    if (state.isAir()) {
                        continue;
                    }
                    if (filter.blocks().contains(BuiltInRegistries.BLOCK.getKey(state.getBlock()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
