package com.sxilverr.effectcontrol;

public record Range(int min, int max) {
    public static final Range ANY = new Range(Integer.MIN_VALUE, Integer.MAX_VALUE);

    public boolean bounded() {
        return min != Integer.MIN_VALUE || max != Integer.MAX_VALUE;
    }

    public boolean contains(int value) {
        return value >= min && value <= max;
    }

    public static Range parse(String raw) {
        String text = raw.trim();
        int dots = text.indexOf("..");
        if (dots < 0) {
            Integer exact = number(text);
            return exact == null ? null : new Range(exact, exact);
        }
        String low = text.substring(0, dots).trim();
        String high = text.substring(dots + 2).trim();
        int min = Integer.MIN_VALUE;
        int max = Integer.MAX_VALUE;
        if (!low.isEmpty()) {
            Integer parsed = number(low);
            if (parsed == null) {
                return null;
            }
            min = parsed;
        }
        if (!high.isEmpty()) {
            Integer parsed = number(high);
            if (parsed == null) {
                return null;
            }
            max = parsed;
        }
        return min > max ? null : new Range(min, max);
    }

    private static Integer number(String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
