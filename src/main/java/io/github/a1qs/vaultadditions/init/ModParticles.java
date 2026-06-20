package io.github.a1qs.vaultadditions.init;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.client.particle.ShieldWallParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = VaultAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;

    public static final RegistryObject<SimpleParticleType> SHIELD_WALL_PARTICLE;


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticles(ParticleFactoryRegisterEvent event) {
        ParticleEngine particleManager = Minecraft.getInstance().particleEngine;

        particleManager.register(SHIELD_WALL_PARTICLE.get(), ShieldWallParticle.Provider::new);
    }

    static {
        PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, VaultAdditions.MOD_ID);
        SHIELD_WALL_PARTICLE = PARTICLE_TYPES.register("shield_wall", () -> new SimpleParticleType(true));
    }
}
