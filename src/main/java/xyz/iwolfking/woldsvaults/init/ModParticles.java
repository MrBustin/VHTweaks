package xyz.iwolfking.woldsvaults.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.event.RegistryEvent;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.particle.ShieldWallParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = WoldsVaults.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModParticles {
    public static final SimpleParticleType BLOOD_PARTICLE = simple("blood", true);

    private static SimpleParticleType simple(String name, boolean overrideLimiter) {
        SimpleParticleType type = new SimpleParticleType(overrideLimiter);
        type.setRegistryName(WoldsVaults.id(name));
        return type;
    }

    public static void register(RegistryEvent.Register<ParticleType<?>> event) {
        event.getRegistry().registerAll(BLOOD_PARTICLE);
    }

    // Merged from the former init.additions class.
public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;

    public static final RegistryObject<SimpleParticleType> SHIELD_WALL_PARTICLE;


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticles(ParticleFactoryRegisterEvent event) {
        ParticleEngine particleManager = Minecraft.getInstance().particleEngine;

        particleManager.register(SHIELD_WALL_PARTICLE.get(), ShieldWallParticle.Provider::new);
    }

    static {
        PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, WoldsVaults.MOD_ID);
        SHIELD_WALL_PARTICLE = PARTICLE_TYPES.register("shield_wall", () -> new SimpleParticleType(true));
    }
}
