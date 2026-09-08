package com.sxilverr.effectcontrol;

import java.util.List;

public record FlagOps(
        List<String> ifSet,
        List<String> ifUnset,
        List<String> set,
        List<String> unset
) {
    public static final FlagOps NONE = new FlagOps(List.of(), List.of(), List.of(), List.of());

    public boolean writes() {
        return !set.isEmpty() || !unset.isEmpty();
    }
}
