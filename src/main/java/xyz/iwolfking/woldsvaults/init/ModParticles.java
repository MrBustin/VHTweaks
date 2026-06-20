package xyz.iwolfking.woldsvaults.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.event.RegistryEvent;
import xyz.iwolfking.woldsvaults.WoldsVaults;

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
}
