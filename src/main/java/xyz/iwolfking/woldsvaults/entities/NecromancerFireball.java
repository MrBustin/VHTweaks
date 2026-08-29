package xyz.iwolfking.woldsvaults.entities;

import iskallia.vault.entity.entity.VaultFireball;
import iskallia.vault.init.ModSounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class NecromancerFireball extends VaultFireball {
    private static final float DAMAGE = 6.0F;

    public NecromancerFireball(Level level, LivingEntity owner) {
        super(level, owner, DAMAGE);
        this.pickup = AbstractArrow.Pickup.DISALLOWED;
        this.setAllowPvP(true);
        this.setType(FireballType.BASE);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return entity instanceof LivingEntity
                && entity != this.getOwner()
                && super.canHitEntity(entity);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!this.level.isClientSide) {
            if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity target) {
                this.hurtTarget(target);
            }

            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 0.6F, 1.25F);
            this.discard();
        }
    }

    private void hurtTarget(LivingEntity target) {
        Entity owner = this.getOwner();
        if (!target.isAlive() || target.isInvulnerable() || target == owner) {
            return;
        }

        if (owner instanceof LivingEntity livingOwner) {
            if (livingOwner.isAlliedTo(target)) {
                return;
            }

            if (target.hurt(DamageSource.indirectMagic(this, livingOwner), DAMAGE)) {
                target.setSecondsOnFire(2);
            }
        } if (target.hurt(DamageSource.MAGIC, DAMAGE)) {
            target.setSecondsOnFire(2);
        }
    }
}
