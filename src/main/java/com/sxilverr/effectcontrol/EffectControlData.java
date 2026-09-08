package com.sxilverr.effectcontrol;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EffectControlData extends SavedData {
    private static final String FILE_ID = EffectControl.MOD_ID;
    private static final String FLAG_PREFIX = "flag:";

    private final Map<UUID, Map<String, Integer>> counts = new HashMap<>();

    //? if >=1.20.5 {
    /*public static EffectControlData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<EffectControlData>(
                        EffectControlData::new, (tag, registries) -> load(tag), null), FILE_ID);
    }
    *///?} else {
    public static EffectControlData get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(EffectControlData::load, EffectControlData::new, FILE_ID);
    }
    //?}

    public int count(UUID player, String key) {
        Map<String, Integer> rules = counts.get(player);
        if (rules == null) {
            return 0;
        }
        return rules.getOrDefault(key, 0);
    }

    public void increment(UUID player, String key) {
        counts.computeIfAbsent(player, id -> new HashMap<>()).merge(key, 1, Integer::sum);
        setDirty();
    }

    public boolean hasFlag(UUID player, String name) {
        return count(player, FLAG_PREFIX + name) > 0;
    }

    public void setFlag(UUID player, String name) {
        counts.computeIfAbsent(player, id -> new HashMap<>()).put(FLAG_PREFIX + name, 1);
        setDirty();
    }

    public void clearFlag(UUID player, String name) {
        Map<String, Integer> values = counts.get(player);
        if (values != null && values.remove(FLAG_PREFIX + name) != null) {
            setDirty();
        }
    }

    public void reset(UUID player) {
        if (counts.remove(player) != null) {
            setDirty();
        }
    }

    public void resetAll() {
        if (!counts.isEmpty()) {
            counts.clear();
            setDirty();
        }
    }

    //? if >=1.20.5 {
    /*@Override
    public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        return write(tag);
    }
    *///?} else {
    @Override
    public CompoundTag save(CompoundTag tag) {
        return write(tag);
    }
    //?}

    private CompoundTag write(CompoundTag tag) {
        ListTag players = new ListTag();
        for (Map.Entry<UUID, Map<String, Integer>> entry : counts.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            CompoundTag player = new CompoundTag();
            player.putUUID("player", entry.getKey());
            CompoundTag rules = new CompoundTag();
            entry.getValue().forEach(rules::putInt);
            player.put("rules", rules);
            players.add(player);
        }
        tag.put("players", players);
        return tag;
    }

    public static EffectControlData load(CompoundTag tag) {
        EffectControlData data = new EffectControlData();
        ListTag players = tag.getList("players", Tag.TAG_COMPOUND);
        for (int i = 0; i < players.size(); i++) {
            CompoundTag player = players.getCompound(i);
            if (!player.hasUUID("player")) {
                continue;
            }
            CompoundTag rules = player.getCompound("rules");
            Map<String, Integer> values = new HashMap<>();
            for (String key : rules.getAllKeys()) {
                values.put(key, rules.getInt(key));
            }
            if (!values.isEmpty()) {
                data.counts.put(player.getUUID("player"), values);
            }
        }
        return data;
    }
}
