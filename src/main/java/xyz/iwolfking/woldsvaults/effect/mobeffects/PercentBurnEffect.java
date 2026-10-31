package xyz.iwolfking.woldsvaults.effect.mobeffects;

import iskallia.vault.event.ActiveFlags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.init.ModEffects;

import javax.annotation.Nullable;
import java.util.UUID;

public class PercentBurnEffect extends MobEffect {

    public static final String BURN_ATTACK_SNAPSHOT = "PercentBurnAttackDamage";
    public static final String BURN_SOURCE_UUID = "PercentBurnSourceUUID";
    public static final String BURN_DAMAGE_IS_FINAL = "PercentBurnDamageIsFinal";
    public static final String BURN_IS_ABILITY_POWER = "PercentBurnIsAbilityPower";

    public PercentBurnEffect() {
        this("burn");
    }

    protected PercentBurnEffect(String id) {
        super(MobEffectCategory.HARMFUL, 0xFF4500);
        this.setRegistryName(WoldsVaults.id(id));
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity target, int amplifier) {
        if (target.level.isClientSide) return;

        spawnFireParticles(target);

        CompoundTag data = getBurnData(target);
        if (!data.contains(BURN_ATTACK_SNAPSHOT)) {
            WoldsVaults.LOGGER.info("No burn snapshot");
            return;
        }

        float attackSnapshot = data.getFloat(BURN_ATTACK_SNAPSHOT);
        if (attackSnapshot <= 0.0f) {
            WoldsVaults.LOGGER.info("Snapshot of 0 damage");
            return;
        }

        float damage = getDamage(data, amplifier);

        DamageSource source;

        if (data.hasUUID(BURN_SOURCE_UUID) && target.level instanceof ServerLevel serverLevel) {
            UUID sourceId = data.getUUID(BURN_SOURCE_UUID);
            Entity sourceEntity = serverLevel.getEntity(sourceId);

            if (sourceEntity instanceof ServerPlayer serverPlayer) {
                source = DamageSource.playerAttack(serverPlayer);
            } else if (sourceEntity instanceof LivingEntity livingSource) {
                source = DamageSource.mobAttack(livingSource);
            } else {
                source = DamageSource.MAGIC;
            }
        } else {
            source = DamageSource.MAGIC;
        }

        Runnable dealDamage = () -> ActiveFlags.IS_DOT_ATTACKING.runIfNotSet(
                () -> target.hurt(source, damage)
        );

        if (data.getBoolean(BURN_IS_ABILITY_POWER)) {
            ActiveFlags.IS_AP_ATTACKING.runIfNotSet(dealDamage);
        } else {
            dealDamage.run();
        }
    }

    private void spawnFireParticles(LivingEntity entity) {
        if (!(entity.level instanceof ServerLevel level)) return;

        level.sendParticles(
                ParticleTypes.FLAME,
                entity.getX(),
                entity.getY() + entity.getBbHeight() * 0.6,
                entity.getZ(),
                2,
                0.2, 0.3, 0.2,
                0.01
        );
    }

    public static void applyBurnEffect(LivingEntity target, int durationTicks, int amplifier, float snapshotValue, @Nullable LivingEntity attacker) {
        CompoundTag data = target.getPersistentData();
        data.putFloat(BURN_ATTACK_SNAPSHOT, snapshotValue);
        data.putBoolean(BURN_DAMAGE_IS_FINAL, false);
        data.putBoolean(BURN_IS_ABILITY_POWER, false);

        if (attacker != null) {
            data.putUUID(BURN_SOURCE_UUID, attacker.getUUID());
        } else {
            data.remove(BURN_SOURCE_UUID);
        }

        target.addEffect(new MobEffectInstance(ModEffects.BURN, durationTicks, amplifier, false, true, true));
    }

    protected CompoundTag getBurnData(LivingEntity target) {
        return target.getPersistentData();
    }

    protected float getDamage(CompoundTag data, int amplifier) {
        float snapshot = data.getFloat(BURN_ATTACK_SNAPSHOT);
        return data.getBoolean(BURN_DAMAGE_IS_FINAL)
                ? snapshot
                : snapshot * (0.10f + (0.10f * amplifier));
    }

    public static void applyAbilityPowerBurn(LivingEntity target, int durationTicks, float damagePerTick,
                                             @Nullable LivingEntity attacker) {
        CompoundTag data = target.getPersistentData();
        data.putFloat(BURN_ATTACK_SNAPSHOT, damagePerTick);
        data.putBoolean(BURN_DAMAGE_IS_FINAL, true);
        data.putBoolean(BURN_IS_ABILITY_POWER, true);

        if (attacker != null) {
            data.putUUID(BURN_SOURCE_UUID, attacker.getUUID());
        } else {
            data.remove(BURN_SOURCE_UUID);
        }

        target.addEffect(new MobEffectInstance(ModEffects.BURN, durationTicks, 0, false, true, true));
    }

    public static void applyBurnEffect(LivingEntity target, int durationTicks, float snapshotValue, @Nullable LivingEntity attacker) {
        applyBurnEffect(target, durationTicks, 0, snapshotValue, attacker);
    }

    public static void applyPercentBurn(LivingEntity target, LivingEntity attacker, int durationTicks, double snapshotValue) {
        applyBurnEffect(target, durationTicks, (float) snapshotValue, attacker);
    }

    public static void applyPercentBurn(LivingEntity target, LivingEntity attacker, int durationTicks, Attribute scalingAttribute) {
        double damage = attacker != null ? attacker.getAttributeValue(scalingAttribute) : 0.0;
        applyPercentBurn(target, attacker, durationTicks, damage);
    }

    public static void applyPercentBurn(LivingEntity target, LivingEntity attacker, int durationTicks) {
        applyPercentBurn(target, attacker, durationTicks, Attributes.ATTACK_DAMAGE);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        super.removeAttributeModifiers(entity, attributes, amplifier);
        CompoundTag data = entity.getPersistentData();
        data.remove(BURN_ATTACK_SNAPSHOT);
        data.remove(BURN_SOURCE_UUID);
        data.remove(BURN_DAMAGE_IS_FINAL);
        data.remove(BURN_IS_ABILITY_POWER);
    }
}
