package com.sxilverr.effectcontrol.neoforge;

import com.sxilverr.effectcontrol.EffectControl;
import com.sxilverr.effectcontrol.EffectControlCommand;
import com.sxilverr.effectcontrol.DelayedEffects;
import com.sxilverr.effectcontrol.EffectApplier;
import com.sxilverr.effectcontrol.EventKind;
import com.sxilverr.effectcontrol.StateScanner;
import com.sxilverr.effectcontrol.TriggerContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = EffectControl.MOD_ID)
public final class EffectControlNeoEvents {
    private EffectControlNeoEvents() {
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.spawn(EventKind.JOIN, dimension(player)));
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        StateScanner.forget(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EventKind kind = event.isEndConquered() ? EventKind.END_RETURN : EventKind.RESPAWN;
            StateScanner.forget(player.getUUID());
            EffectApplier.apply(player, TriggerContext.spawn(kind, dimension(player)));
        }
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            StateScanner.forget(player.getUUID());
            EffectApplier.apply(player, TriggerContext.dimension(
                    event.getFrom().location(), event.getTo().location()));
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (event.getSource().getEntity() instanceof ServerPlayer player && player != victim) {
            EffectApplier.apply(player, TriggerContext.entity(EventKind.KILL, dimension(player),
                    BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType()), event.getSource().getMsgId()));
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingDamageEvent.Pre event) {
        LivingEntity victim = event.getEntity();
        String source = event.getSource().getMsgId();
        if (event.getSource().getEntity() instanceof ServerPlayer attacker && attacker != victim) {
            EffectApplier.apply(attacker, TriggerContext.entity(EventKind.HURT, dimension(attacker),
                    BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType()), source));
        }
        if (victim instanceof ServerPlayer player) {
            ResourceLocation from = event.getSource().getEntity() == null ? null
                    : BuiltInRegistries.ENTITY_TYPE.getKey(event.getSource().getEntity().getType());
            EffectApplier.apply(player, TriggerContext.entity(EventKind.HURT_BY, dimension(player), from, source));
        }
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.item(EventKind.USE, dimension(player),
                    event.getItemStack(), null));
        }
    }

    @SubscribeEvent
    public static void onConsume(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.item(EventKind.CONSUME, dimension(player),
                    event.getItem(), null));
        }
    }

    @SubscribeEvent
    public static void onShoot(ArrowLooseEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.item(EventKind.SHOOT, dimension(player),
                    event.getBow(), null));
        }
    }

    @SubscribeEvent
    public static void onRelease(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.item(EventKind.RELEASE, dimension(player),
                    event.getItem(), null));
        }
    }

    @SubscribeEvent
    public static void onPickup(ItemEntityPickupEvent.Post event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.item(EventKind.PICKUP, dimension(player),
                    event.getOriginalStack(), null));
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.spawn(EventKind.CONTAINER, dimension(player)));
        }
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.advancement(dimension(player),
                    event.getAdvancement().id()));
        }
    }

    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.item(EventKind.CRAFT, dimension(player),
                    event.getCrafting(), null));
        }
    }

    @SubscribeEvent
    public static void onSmelt(PlayerEvent.ItemSmeltedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.item(EventKind.SMELT, dimension(player),
                    event.getSmelting(), null));
        }
    }

    @SubscribeEvent
    public static void onFish(ItemFishedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.item(EventKind.FISH, dimension(player),
                    event.getDrops().isEmpty() ? null : event.getDrops().get(0), null));
        }
    }

    @SubscribeEvent
    public static void onTame(AnimalTameEvent event) {
        if (event.getTamer() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.entity(EventKind.TAME, dimension(player),
                    BuiltInRegistries.ENTITY_TYPE.getKey(event.getAnimal().getType()), null));
        }
    }

    @SubscribeEvent
    public static void onBreed(BabyEntitySpawnEvent event) {
        if (event.getCausedByPlayer() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.entity(EventKind.BREED, dimension(player),
                    BuiltInRegistries.ENTITY_TYPE.getKey(event.getParentA().getType()), null));
        }
    }

    @SubscribeEvent
    public static void onTrade(TradeWithVillagerEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.item(EventKind.TRADE, dimension(player),
                    event.getMerchantOffer().getResult(), null));
        }
    }

    @SubscribeEvent
    public static void onLevelUp(PlayerXpEvent.LevelChange event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getLevels() > 0) {
            EffectApplier.apply(player, TriggerContext.spawn(EventKind.LEVEL_UP, dimension(player)));
        }
    }

    @SubscribeEvent
    public static void onToolBroken(PlayerDestroyItemEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EffectApplier.apply(player, TriggerContext.item(EventKind.TOOL_BROKEN, dimension(player),
                    event.getOriginal(), null));
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        EffectControlCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        DelayedEffects.tick(event.getServer());
        StateScanner.tick(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        DelayedEffects.clear();
        StateScanner.clear();
    }

    private static ResourceLocation dimension(ServerPlayer player) {
        return player.level().dimension().location();
    }
}
