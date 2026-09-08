package com.sxilverr.effectcontrol;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;

import java.util.Locale;

public final class Environment {
    private static final long DAY_LENGTH = 24000L;

    private Environment() {
    }

    public static boolean matches(ServerPlayer player, WorldFilter filter) {
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();
        long dayTime = level.getDayTime();

        if (filter.wantsCoords()
                && !(filter.x().contains(pos.getX())
                && filter.y().contains(pos.getY())
                && filter.z().contains(pos.getZ()))) {
            return false;
        }
        if (filter.time().bounded() && !filter.time().contains((int) Math.floorMod(dayTime, DAY_LENGTH))) {
            return false;
        }
        if (filter.days().bounded() && !filter.days().contains((int) Math.floorDiv(dayTime, DAY_LENGTH))) {
            return false;
        }
        if (filter.health().bounded() && !filter.health().contains((int) Math.floor(player.getHealth()))) {
            return false;
        }
        if (filter.xpLevel().bounded() && !filter.xpLevel().contains(player.experienceLevel)) {
            return false;
        }
        if (filter.light().bounded() && !filter.light().contains(level.getMaxLocalRawBrightness(pos))) {
            return false;
        }
        if (!filter.weather().isEmpty() && !filter.weather().contains(weather(level))) {
            return false;
        }
        if (!filter.difficulties().isEmpty()
                && !filter.difficulties().contains(level.getDifficulty().getKey().toLowerCase(Locale.ROOT))) {
            return false;
        }
        if (!filter.gamemodes().isEmpty()
                && !filter.gamemodes().contains(player.gameMode.getGameModeForPlayer().getName())) {
            return false;
        }
        if (!filter.biomes().isEmpty() && !matchesBiome(level, pos, filter)) {
            return false;
        }
        if (!filter.tags().isEmpty() && filter.tags().stream().noneMatch(player.getTags()::contains)) {
            return false;
        }
        if (!filter.wearing().isEmpty() && !Equipment.isWearing(player, filter.wearing(), null)) {
            return false;
        }
        if (filter.statId() != null && !matchesStat(player, filter)) {
            return false;
        }
        return filter.chance() >= 100 || player.getRandom().nextInt(100) < filter.chance();
    }

    public static String weather(ServerLevel level) {
        if (level.isThundering()) {
            return "thunder";
        }
        return level.isRaining() ? "rain" : "clear";
    }

    private static boolean matchesBiome(ServerLevel level, BlockPos pos, WorldFilter filter) {
        return level.getBiome(pos).unwrapKey()
                .map(key -> filter.biomes().contains(key.location()))
                .orElse(false);
    }

    private static boolean matchesStat(ServerPlayer player, WorldFilter filter) {
        Stat<?> stat = resolveStat(filter.statType(), filter.statId());
        return stat != null && filter.statCount().contains(player.getStats().getValue(stat));
    }

    private static Stat<?> resolveStat(String type, ResourceLocation id) {
        return switch (type) {
            case "mined" -> BuiltInRegistries.BLOCK.getOptional(id).map(Stats.BLOCK_MINED::get).orElse(null);
            case "killed" -> BuiltInRegistries.ENTITY_TYPE.getOptional(id).map(Stats.ENTITY_KILLED::get).orElse(null);
            case "killed_by" -> BuiltInRegistries.ENTITY_TYPE.getOptional(id).map(Stats.ENTITY_KILLED_BY::get).orElse(null);
            case "used" -> BuiltInRegistries.ITEM.getOptional(id).map(Stats.ITEM_USED::get).orElse(null);
            case "crafted" -> BuiltInRegistries.ITEM.getOptional(id).map(Stats.ITEM_CRAFTED::get).orElse(null);
            case "broken" -> BuiltInRegistries.ITEM.getOptional(id).map(Stats.ITEM_BROKEN::get).orElse(null);
            case "picked_up" -> BuiltInRegistries.ITEM.getOptional(id).map(Stats.ITEM_PICKED_UP::get).orElse(null);
            case "dropped" -> BuiltInRegistries.ITEM.getOptional(id).map(Stats.ITEM_DROPPED::get).orElse(null);
            case "custom" -> Stats.CUSTOM.get(id);
            default -> null;
        };
    }
}
