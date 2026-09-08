package com.sxilverr.effectcontrol;

import java.util.Locale;

public enum EffectAction {
    GIVE,
    REMOVE;

    public static EffectAction byName(String name) {
        return switch (name.trim().toLowerCase(Locale.ROOT)) {
            case "give", "add", "grant" -> GIVE;
            case "remove", "clear", "take" -> REMOVE;
            default -> null;
        };
    }
}
