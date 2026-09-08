package com.sxilverr.effectcontrol;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public record EffectRule(
        String key,
        EffectSpec effect,
        Trigger trigger,
        RuleFilter filter,
        WorldFilter world,
        EffectAction action,
        List<ResourceLocation> requires,
        List<ResourceLocation> missing,
        FlagOps flags,
        int limit
) {
    public static final int MAX_DURATION_SECONDS = 1_000_000;
    public static final int MAX_LEVEL = 256;
    public static final int MAX_RADIUS = 64;

    private static final List<String> STAT_TYPES = List.of(
            "mined", "killed", "killed_by", "used", "crafted", "broken", "picked_up", "dropped", "custom");
    private static final List<String> WEATHERS = List.of("clear", "rain", "thunder");
    private static final List<String> FLUIDS = List.of("water", "lava");
    private static final List<String> GAMEMODES = List.of("survival", "creative", "adventure", "spectator");
    private static final List<String> DIFFICULTIES = List.of("peaceful", "easy", "normal", "hard");

    public boolean matches(TriggerContext context) {
        if (!trigger.matches(context.kind())) {
            return false;
        }
        if (context.ruleKey() != null && !context.ruleKey().equals(key)) {
            return false;
        }
        return filter.matches(context, trigger);
    }

    public static EffectRule parse(String raw) {
        if (raw == null) {
            return null;
        }
        String text = raw.trim();
        if (text.isEmpty() || text.startsWith("#")) {
            return null;
        }

        ResourceLocation effect = null;
        int durationTicks = 30 * 20;
        int level = 1;
        Trigger trigger = Trigger.RESPAWN;
        EffectAction action = EffectAction.GIVE;
        ResourceLocation dimension = null;
        CompoundTag nbt = null;
        List<ResourceLocation> blocks = new ArrayList<>();
        List<ResourceLocation> entities = new ArrayList<>();
        List<ResourceLocation> items = new ArrayList<>();
        List<String> sources = new ArrayList<>();
        List<ResourceLocation> advancements = new ArrayList<>();
        List<String> fluids = new ArrayList<>();
        List<ResourceLocation> requires = new ArrayList<>();
        List<ResourceLocation> missing = new ArrayList<>();
        List<String> ifSet = new ArrayList<>();
        List<String> ifUnset = new ArrayList<>();
        List<String> setFlags = new ArrayList<>();
        List<String> unsetFlags = new ArrayList<>();
        List<String> weather = new ArrayList<>();
        List<ResourceLocation> biomes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        List<String> gamemodes = new ArrayList<>();
        List<String> difficulties = new ArrayList<>();
        List<ResourceLocation> wearing = new ArrayList<>();
        Range x = Range.ANY;
        Range y = Range.ANY;
        Range z = Range.ANY;
        Range time = Range.ANY;
        Range days = Range.ANY;
        Range health = Range.ANY;
        Range xpLevel = Range.ANY;
        Range light = Range.ANY;
        Range statCount = new Range(1, Integer.MAX_VALUE);
        String statType = null;
        ResourceLocation statId = null;
        int chance = 100;
        int cooldown = 0;
        int radius = 0;
        int limit = 0;
        boolean ambient = false;
        boolean particles = true;
        boolean icon = true;
        boolean hidden = false;
        String id = null;

        for (String part : tokenize(text)) {
            String token = part.trim();
            if (token.isEmpty()) {
                continue;
            }
            int split = token.indexOf('=');
            if (split <= 0) {
                return null;
            }
            String name = token.substring(0, split).trim().toLowerCase(Locale.ROOT);
            String value = token.substring(split + 1).trim();
            if (value.isEmpty()) {
                return null;
            }
            switch (name) {
                case "effect" -> {
                    effect = parseId(value);
                    if (effect == null) {
                        return null;
                    }
                }
                case "duration" -> {
                    durationTicks = parseDurationTicks(value);
                    if (durationTicks == 0) {
                        return null;
                    }
                }
                case "level" -> {
                    level = parseInt(value);
                    if (level < 1 || level > MAX_LEVEL) {
                        return null;
                    }
                }
                case "trigger" -> {
                    trigger = Trigger.byName(value);
                    if (trigger == null) {
                        return null;
                    }
                }
                case "action" -> {
                    action = EffectAction.byName(value);
                    if (action == null) {
                        return null;
                    }
                }
                case "dimension" -> {
                    dimension = parseId(value);
                    if (dimension == null) {
                        return null;
                    }
                }
                case "block" -> {
                    if (!addId(blocks, value)) {
                        return null;
                    }
                }
                case "entity", "mob" -> {
                    if (!addId(entities, value)) {
                        return null;
                    }
                }
                case "item" -> {
                    if (!addId(items, value)) {
                        return null;
                    }
                }
                case "advancement" -> {
                    if (!addId(advancements, value)) {
                        return null;
                    }
                }
                case "biome" -> {
                    if (!addId(biomes, value)) {
                        return null;
                    }
                }
                case "wearing", "armor" -> {
                    if (!addId(wearing, value)) {
                        return null;
                    }
                }
                case "requires", "has" -> {
                    if (!addId(requires, value)) {
                        return null;
                    }
                }
                case "missing", "without" -> {
                    if (!addId(missing, value)) {
                        return null;
                    }
                }
                case "source", "damage_type" -> sources.add(value.toLowerCase(Locale.ROOT));
                case "tag" -> tags.add(value);
                case "fluid", "liquid" -> {
                    if (!addWord(fluids, value, FLUIDS)) {
                        return null;
                    }
                }
                case "weather" -> {
                    if (!addWord(weather, value, WEATHERS)) {
                        return null;
                    }
                }
                case "gamemode" -> {
                    if (!addWord(gamemodes, value, GAMEMODES)) {
                        return null;
                    }
                }
                case "difficulty" -> {
                    if (!addWord(difficulties, value, DIFFICULTIES)) {
                        return null;
                    }
                }
                case "nbt" -> {
                    nbt = parseNbt(value);
                    if (nbt == null) {
                        return null;
                    }
                }
                case "if_set", "flagged" -> ifSet.add(value);
                case "if_unset", "unflagged" -> ifUnset.add(value);
                case "set", "set_flag" -> setFlags.add(value);
                case "unset", "clear_flag" -> unsetFlags.add(value);
                case "x" -> {
                    x = Range.parse(value);
                    if (x == null) {
                        return null;
                    }
                }
                case "y" -> {
                    y = Range.parse(value);
                    if (y == null) {
                        return null;
                    }
                }
                case "z" -> {
                    z = Range.parse(value);
                    if (z == null) {
                        return null;
                    }
                }
                case "time" -> {
                    time = parseTime(value);
                    if (time == null) {
                        return null;
                    }
                }
                case "days" -> {
                    days = Range.parse(value);
                    if (days == null) {
                        return null;
                    }
                }
                case "health" -> {
                    health = Range.parse(value);
                    if (health == null) {
                        return null;
                    }
                }
                case "xp_level", "xp" -> {
                    xpLevel = Range.parse(value);
                    if (xpLevel == null) {
                        return null;
                    }
                }
                case "light" -> {
                    light = Range.parse(value);
                    if (light == null) {
                        return null;
                    }
                }
                case "stat" -> {
                    int mark = value.indexOf(':');
                    if (mark <= 0) {
                        return null;
                    }
                    statType = value.substring(0, mark).trim().toLowerCase(Locale.ROOT);
                    statId = parseId(value.substring(mark + 1).trim());
                    if (statId == null || !STAT_TYPES.contains(statType)) {
                        return null;
                    }
                }
                case "count" -> {
                    statCount = Range.parse(value);
                    if (statCount == null) {
                        return null;
                    }
                }
                case "chance" -> {
                    chance = parseInt(value);
                    if (chance < 1 || chance > 100) {
                        return null;
                    }
                }
                case "cooldown" -> {
                    int seconds = parseInt(value);
                    if (seconds < 1) {
                        return null;
                    }
                    cooldown = seconds * 20;
                }
                case "radius" -> {
                    radius = parseInt(value);
                    if (radius < 1 || radius > MAX_RADIUS) {
                        return null;
                    }
                }
                case "limit" -> {
                    limit = parseInt(value);
                    if (limit < 0) {
                        return null;
                    }
                }
                case "ambient" -> {
                    Boolean parsed = parseBoolean(value);
                    if (parsed == null) {
                        return null;
                    }
                    ambient = parsed;
                }
                case "particles" -> {
                    Boolean parsed = parseBoolean(value);
                    if (parsed == null) {
                        return null;
                    }
                    particles = parsed;
                }
                case "icon" -> {
                    Boolean parsed = parseBoolean(value);
                    if (parsed == null) {
                        return null;
                    }
                    icon = parsed;
                }
                case "hidden" -> {
                    Boolean parsed = parseBoolean(value);
                    if (parsed == null) {
                        return null;
                    }
                    hidden = parsed;
                }
                case "id" -> id = value;
                default -> {
                    return null;
                }
            }
        }

        FlagOps flags = new FlagOps(List.copyOf(ifSet), List.copyOf(ifUnset),
                List.copyOf(setFlags), List.copyOf(unsetFlags));
        if (effect == null && !flags.writes()) {
            return null;
        }
        if (trigger.needsDimension() && dimension == null) {
            return null;
        }
        if (!validState(trigger, radius, blocks, entities, fluids)) {
            return null;
        }
        if (trigger == Trigger.FIRST_JOIN) {
            limit = 1;
        }
        if (hidden) {
            particles = false;
            icon = false;
        }

        RuleFilter filter = new RuleFilter(dimension, List.copyOf(blocks), List.copyOf(entities),
                List.copyOf(items), List.copyOf(sources), List.copyOf(advancements),
                List.copyOf(fluids), nbt, radius);
        WorldFilter world = new WorldFilter(x, y, z, time, days, health, xpLevel, light,
                List.copyOf(weather), List.copyOf(biomes), List.copyOf(tags), List.copyOf(gamemodes),
                List.copyOf(difficulties), List.copyOf(wearing), statType, statId, statCount, chance, cooldown);
        EffectSpec spec = effect == null ? null
                : new EffectSpec(effect, durationTicks, level - 1, ambient, particles, icon);
        String counterKey = id != null ? id : defaultKey(effect, trigger, filter, world);
        return new EffectRule(counterKey, spec, trigger, filter, world, action,
                List.copyOf(requires), List.copyOf(missing), flags, limit);
    }

    private static boolean validState(Trigger trigger, int radius, List<ResourceLocation> blocks,
                                      List<ResourceLocation> entities, List<String> fluids) {
        return switch (trigger.state()) {
            case PROXIMITY -> radius >= 1 && !(blocks.isEmpty() && entities.isEmpty());
            case LOOKING -> !(blocks.isEmpty() && entities.isEmpty());
            case IN_FLUID -> !fluids.isEmpty();
            default -> true;
        };
    }

    private static String defaultKey(ResourceLocation effect, Trigger trigger, RuleFilter filter, WorldFilter world) {
        StringBuilder builder = new StringBuilder(effect == null ? "-" : effect.toString())
                .append('/').append(trigger.id());
        if (filter.dimension() != null) {
            builder.append('/').append(filter.dimension());
        }
        appendAll(builder, filter.blocks());
        appendAll(builder, filter.entities());
        appendAll(builder, filter.items());
        appendAll(builder, filter.advancements());
        for (String source : filter.sources()) {
            builder.append('/').append(source);
        }
        for (String fluid : filter.fluids()) {
            builder.append('/').append(fluid);
        }
        if (filter.nbt() != null) {
            builder.append('/').append(filter.nbt());
        }
        if (filter.radius() > 0) {
            builder.append("/r").append(filter.radius());
        }
        if (world.wantsCoords()) {
            builder.append("/@").append(world.x().min()).append(':').append(world.x().max())
                    .append(':').append(world.y().min()).append(':').append(world.y().max())
                    .append(':').append(world.z().min()).append(':').append(world.z().max());
        }
        if (world.time().bounded()) {
            builder.append("/t").append(world.time().min()).append(':').append(world.time().max());
        }
        for (String value : world.weather()) {
            builder.append('/').append(value);
        }
        return builder.toString();
    }

    private static void appendAll(StringBuilder builder, List<ResourceLocation> ids) {
        for (ResourceLocation id : ids) {
            builder.append('/').append(id);
        }
    }

    private static boolean addWord(List<String> target, String value, List<String> allowed) {
        String word = value.toLowerCase(Locale.ROOT);
        if (!allowed.contains(word)) {
            return false;
        }
        target.add(word);
        return true;
    }

    private static boolean addId(List<ResourceLocation> target, String value) {
        ResourceLocation id = parseId(value);
        if (id == null) {
            return false;
        }
        target.add(id);
        return true;
    }

    private static List<String> tokenize(String text) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;
        char quote = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (quote != 0) {
                current.append(c);
                if (c == '\\' && i + 1 < text.length()) {
                    current.append(text.charAt(++i));
                } else if (c == quote) {
                    quote = 0;
                }
                continue;
            }
            if (c == '\'' || c == '"') {
                quote = c;
                current.append(c);
            } else if (c == '{' || c == '[') {
                depth++;
                current.append(c);
            } else if (c == '}' || c == ']') {
                depth--;
                current.append(c);
            } else if (c == ',' && depth == 0) {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        parts.add(current.toString());
        return parts;
    }

    private static CompoundTag parseNbt(String value) {
        try {
            return TagParser.parseTag(value);
        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    private static Range parseTime(String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "day" -> new Range(0, 11999);
            case "night" -> new Range(12000, 23999);
            case "noon" -> new Range(5000, 7000);
            case "midnight" -> new Range(17000, 19000);
            case "sunrise", "dawn" -> new Range(22500, 23500);
            case "sunset", "dusk" -> new Range(11500, 12500);
            default -> Range.parse(value);
        };
    }

    private static ResourceLocation parseId(String value) {
        return ResourceLocation.tryParse(value.indexOf(':') < 0 ? "minecraft:" + value : value);
    }

    private static int parseDurationTicks(String value) {
        String text = value.toLowerCase(Locale.ROOT);
        if (text.equals("infinite") || text.equals("inf") || text.equals("permanent")) {
            return MobEffectInstance.INFINITE_DURATION;
        }
        int seconds = parseInt(text);
        if (seconds < 1) {
            return 0;
        }
        return Math.min(seconds, MAX_DURATION_SECONDS) * 20;
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static Boolean parseBoolean(String value) {
        String text = value.toLowerCase(Locale.ROOT);
        if (text.equals("true") || text.equals("yes")) {
            return Boolean.TRUE;
        }
        if (text.equals("false") || text.equals("no")) {
            return Boolean.FALSE;
        }
        return null;
    }
}
