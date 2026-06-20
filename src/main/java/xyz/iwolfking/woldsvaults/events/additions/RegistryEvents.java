package xyz.iwolfking.woldsvaults.events.additions;

import xyz.iwolfking.woldsvaults.init.additions.ModEffects;
import xyz.iwolfking.woldsvaults.init.additions.ModEntities;
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
