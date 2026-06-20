package xyz.iwolfking.woldsvaults.effect.mobeffects;

import iskallia.vault.event.ActiveFlags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.init.ModEffects;
import xyz.iwolfking.woldsvaults.init.ModParticles;

import java.util.UUID;

public class HemorrhagedEffect extends MobEffect {
    public static final String ID = "hemorrhaged";

    public static final int COLOR = 0x6e0000;
    public static final int STACK_DURATION = 5 * 20;
    public static final int MAX_STACKS = 5;

    public static final int BASE_INTERVAL = 25;
    public static final int STACK_INTERVAL_DECREASE = 5;

    public HemorrhagedEffect(MobEffectCategory category, int color, ResourceLocation id) {
        super(category, color);
        this.setRegistryName(id);
    }

    public boolean dealsDamage(int duration, int amplifier) {
        return duration % Math.max(1, BASE_INTERVAL - amplifier * STACK_INTERVAL_DECREASE) == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level.isClientSide || entity.isDeadOrDying()) {
            return;
        }

        MobEffectInstance instance = entity.getEffect(ModEffects.HEMORRHAGED);
        if (instance instanceof HemorrhagedInstance casted && entity.level.getPlayerByUUID(casted.source) instanceof ServerPlayer source) {
            if (dealsDamage(instance.getDuration(), amplifier)) {
                ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
                    float damage = entity.getMaxHealth() * .01F;
                    Vec3 movement = entity.getDeltaMovement();
                    int invulnerableTime = entity.invulnerableTime;
                    entity.invulnerableTime = 0;
                    entity.hurt(createDamageSource(source), damage);
                    entity.setDeltaMovement(movement);
                    entity.invulnerableTime = invulnerableTime;

                    EntityDimensions dimensions = entity.getDimensions(entity.getPose());
                    for (ServerPlayer player : ((ServerLevel) entity.level).players()) {
                        ((ServerLevel) entity.level).sendParticles(
                                player,
                                ModParticles.BLOOD_PARTICLE,
                                true,
                                entity.getX(),
                                entity.getY() + dimensions.height * 0.5F,
                                entity.getZ(),
                                25,
                                dimensions.width / 6,
                                dimensions.height / 4,
                                dimensions.width / 6,
                                0
                        );
                    }
                });
            }

            if (instance.getDuration() == 1 && amplifier > 0) {
                entity.removeEffectNoUpdate(ModEffects.HEMORRHAGED);
                entity.addEffect(new HemorrhagedInstance(
                        casted.source,
                        instance.getEffect(),
                        STACK_DURATION,
                        --amplifier,
                        instance.isAmbient(),
                        instance.isVisible(),
                        instance.showIcon()
                ));
            }
        }
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration == 1 || dealsDamage(duration, amplifier);
    }

    private static DamageSource createDamageSource(ServerPlayer source) {
        return new DamageSource(ID) {
            @Override
            public @Nullable Entity getEntity() {
                return source;
            }
        }.bypassInvul().bypassArmor();
    }

    public static class HemorrhagedInstance extends MobEffectInstance {
        private final UUID source;

        public HemorrhagedInstance(UUID source, MobEffect effect, int duration, int amplifier) {
            super(effect, duration, amplifier);
            this.source = source;
        }

        public HemorrhagedInstance(UUID source, MobEffect p_19528_, int p_19529_, int p_19530_, boolean p_19531_, boolean p_19532_, boolean p_19533_) {
            super(p_19528_, p_19529_, p_19530_, p_19531_, p_19532_, p_19533_);
            this.source = source;
        }
    }
}
