package xyz.iwolfking.woldsvaults.effect.trinket;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.trinket.TrinketEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectOnHitTakenEffect extends TrinketEffect<EffectOnHitTakenEffect.Config> {
    private final MobEffect effect;
    private final int addedAmplifier;
    private final int duration;
    private final float chance;

    public EffectOnHitTakenEffect(ResourceLocation name, float chance, MobEffect effect, int addedAmplifier, int duration) {
        super(name);
        this.effect = effect;
        this.addedAmplifier = addedAmplifier;
        this.chance = chance;
        this.duration = duration;
    }

    public Class<EffectOnHitTakenEffect.Config> getConfigClass() {
        return EffectOnHitTakenEffect.Config.class;
    }

    public EffectOnHitTakenEffect.Config getDefaultConfig() {
        return new EffectOnHitTakenEffect.Config(this.chance, this.effect.getRegistryName(), this.addedAmplifier, duration);
    }

    public static class Config extends TrinketEffect.Config {
        @Expose
        private float chance;

        @Expose
        private int addedAmplifier;

        @Expose
        private int duration;

        @Expose
        private ResourceLocation effect;

        public Config(float chance, ResourceLocation effect, int addedAmplifier, int duration) {
            this.chance = chance;
            this.effect = effect;
            this.addedAmplifier = addedAmplifier;
            this.duration = duration;
        }

        public MobEffect getEffect() {
            return ForgeRegistries.MOB_EFFECTS.getValue(this.effect);
        }

        public MobEffectInstance getEffectInstance() {
            return new MobEffectInstance(getEffect(), duration, addedAmplifier);
        }

        public int getAddedAmplifier() {
            return this.addedAmplifier;
        }

        public int getDuration() {
            return this.duration;
        }


        public float getChance() {
            return this.chance;
        }
    }
}
