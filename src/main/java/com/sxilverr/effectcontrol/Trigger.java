package com.sxilverr.effectcontrol;

import java.util.Locale;

public enum Trigger {
    JOIN(EventKind.JOIN),
    FIRST_JOIN(EventKind.JOIN),
    RESPAWN(EventKind.RESPAWN),
    END_RETURN(EventKind.END_RETURN),
    ANY(null),
    ENTER(EventKind.CHANGE_DIMENSION),
    LEAVE(EventKind.CHANGE_DIMENSION),
    KILL(EventKind.KILL),
    HURT(EventKind.HURT),
    HURT_BY(EventKind.HURT_BY),
    USE(EventKind.USE),
    CONSUME(EventKind.CONSUME),
    SHOOT(EventKind.SHOOT),
    RELEASE(EventKind.RELEASE),
    PICKUP(EventKind.PICKUP),
    CONTAINER(EventKind.CONTAINER),
    ADVANCEMENT(EventKind.ADVANCEMENT),
    CRAFT(EventKind.CRAFT),
    SMELT(EventKind.SMELT),
    FISH(EventKind.FISH),
    TAME(EventKind.TAME),
    BREED(EventKind.BREED),
    TRADE(EventKind.TRADE),
    LEVEL_UP(EventKind.LEVEL_UP),
    TOOL_BROKEN(EventKind.TOOL_BROKEN),
    NEAR(StateKind.PROXIMITY, true),
    AWAY(StateKind.PROXIMITY, false),
    LOOKING(StateKind.LOOKING, true),
    NOT_LOOKING(StateKind.LOOKING, false),
    SNEAKING(StateKind.SNEAKING, true),
    NOT_SNEAKING(StateKind.SNEAKING, false),
    SPRINTING(StateKind.SPRINTING, true),
    NOT_SPRINTING(StateKind.SPRINTING, false),
    ON_FIRE(StateKind.ON_FIRE, true),
    NOT_ON_FIRE(StateKind.ON_FIRE, false),
    IN_FLUID(StateKind.IN_FLUID, true),
    NOT_IN_FLUID(StateKind.IN_FLUID, false),
    RIDING(StateKind.RIDING, true),
    NOT_RIDING(StateKind.RIDING, false),
    SLEEPING(StateKind.SLEEPING, true),
    NOT_SLEEPING(StateKind.SLEEPING, false),
    UNDERWATER(StateKind.UNDERWATER, true),
    NOT_UNDERWATER(StateKind.UNDERWATER, false);

    private final EventKind kind;
    private final StateKind state;
    private final boolean onEnter;

    Trigger(EventKind kind) {
        this.kind = kind;
        this.state = StateKind.NONE;
        this.onEnter = false;
    }

    Trigger(StateKind state, boolean onEnter) {
        this.kind = onEnter ? EventKind.STATE_ON : EventKind.STATE_OFF;
        this.state = state;
        this.onEnter = onEnter;
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public StateKind state() {
        return state;
    }

    public boolean isStateTrigger() {
        return state != StateKind.NONE;
    }

    public boolean firesOnEnter() {
        return onEnter;
    }

    public EventKind stateKind() {
        return onEnter ? EventKind.STATE_ON : EventKind.STATE_OFF;
    }

    public boolean needsDimension() {
        return this == ENTER || this == LEAVE;
    }

    public boolean matches(EventKind other) {
        if (this == ANY) {
            return other == EventKind.JOIN || other == EventKind.RESPAWN || other == EventKind.END_RETURN;
        }
        return kind == other;
    }

    public static Trigger byName(String name) {
        return switch (name.trim().toLowerCase(Locale.ROOT)) {
            case "join", "every_join", "login" -> JOIN;
            case "first_join", "once" -> FIRST_JOIN;
            case "respawn", "every_respawn", "death" -> RESPAWN;
            case "end_return", "end" -> END_RETURN;
            case "any", "always" -> ANY;
            case "enter", "enter_dimension" -> ENTER;
            case "leave", "leave_dimension", "exit" -> LEAVE;
            case "kill" -> KILL;
            case "hurt", "damage", "attack" -> HURT;
            case "hurt_by", "takedamagefrom", "damaged_by" -> HURT_BY;
            case "use", "use_item", "interact" -> USE;
            case "consume", "eat", "drink" -> CONSUME;
            case "shoot", "fire" -> SHOOT;
            case "release" -> RELEASE;
            case "pickup" -> PICKUP;
            case "container", "inventory", "open" -> CONTAINER;
            case "advancement", "achievement" -> ADVANCEMENT;
            case "craft" -> CRAFT;
            case "smelt" -> SMELT;
            case "fish" -> FISH;
            case "tame" -> TAME;
            case "breed" -> BREED;
            case "trade" -> TRADE;
            case "level_up", "levelup" -> LEVEL_UP;
            case "tool_broken", "item_broken" -> TOOL_BROKEN;
            case "near", "when_near" -> NEAR;
            case "away", "not_near", "leave_near" -> AWAY;
            case "looking", "look", "looking_at" -> LOOKING;
            case "not_looking", "look_away" -> NOT_LOOKING;
            case "sneaking", "sneak", "crouching" -> SNEAKING;
            case "not_sneaking" -> NOT_SNEAKING;
            case "sprinting", "sprint" -> SPRINTING;
            case "not_sprinting" -> NOT_SPRINTING;
            case "on_fire", "burning" -> ON_FIRE;
            case "not_on_fire" -> NOT_ON_FIRE;
            case "in_fluid", "in_liquid" -> IN_FLUID;
            case "not_in_fluid", "not_in_liquid" -> NOT_IN_FLUID;
            case "riding", "mounted" -> RIDING;
            case "not_riding", "dismounted" -> NOT_RIDING;
            case "sleeping", "asleep" -> SLEEPING;
            case "not_sleeping", "awake" -> NOT_SLEEPING;
            case "underwater", "submerged" -> UNDERWATER;
            case "not_underwater" -> NOT_UNDERWATER;
            default -> null;
        };
    }
}
