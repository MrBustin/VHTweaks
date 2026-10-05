package xyz.iwolfking.woldsvaults.entities.msc;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class SmallFireballSettable extends SmallFireball {
    private final float damage;

    public SmallFireballSettable(EntityType<? extends SmallFireball> pEntityType, Level pLevel, float damage) {
        super(pEntityType, pLevel);
        this.damage = damage;
    }

    public SmallFireballSettable(Level pLevel, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, float damage) {
        super(pLevel, pShooter, pOffsetX, pOffsetY, pOffsetZ);
        this.damage = damage;
    }

    public SmallFireballSettable(Level pLevel, double pX, double pY, double pZ, double pOffsetX, double pOffsetY, double pOffsetZ, float damage) {
        super(pLevel, pX, pY, pZ, pOffsetX, pOffsetY, pOffsetZ);
        this.damage = damage;
    }

    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide) {
            Entity entity = pResult.getEntity();
            if (!entity.fireImmune()) {
                Entity entity1 = this.getOwner();
                int i = entity.getRemainingFireTicks();
                entity.setSecondsOnFire(5);
                boolean flag = entity.hurt(DamageSource.fireball(this, entity1), damage);
                if (!flag) {
                    entity.setRemainingFireTicks(i);
                } else if (entity1 instanceof LivingEntity) {
                    this.doEnchantDamageEffects((LivingEntity)entity1, entity);
                }
            }

        }
    }

    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
    }
}
