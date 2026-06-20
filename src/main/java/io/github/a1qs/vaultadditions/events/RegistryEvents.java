package io.github.a1qs.vaultadditions.events;

import io.github.a1qs.vaultadditions.init.ModEffects;
import io.github.a1qs.vaultadditions.init.ModEntities;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
    @SubscribeEvent
    public static void onEntityRegister(RegistryEvent.Register<EntityType<?>> event) {
        ModEntities.register(event);
    }
    @SubscribeEvent
    public static void onEffectRegister(RegistryEvent.Register<MobEffect> event) {
        ModEffects.registerEffects(event);
    }
}
