package xyz.iwolfking.woldsvaults.entities.msc;

import iskallia.vault.event.ActiveFlags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import xyz.iwolfking.woldsvaults.effect.mobeffects.PercentBurnEffect;

public class VaultMeteor extends AbstractArrow {
    private static final double HOMING_SPEED = 0.625D;
    private static final EntityDataAccessor<Boolean> ALLOW_PVP =
            SynchedEntityData.defineId(VaultMeteor.class, EntityDataSerializers.BOOLEAN);

    private LivingEntity entityLocked;
    private int burnDuration;
    private int amplifier;
    private float damage;

    public VaultMeteor(EntityType<? extends VaultMeteor> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ALLOW_PVP, false);
    }

    public void setEntityLocked(LivingEntity entityLocked) {
        this.entityLocked = entityLocked;
    }

    public void setBurnDuration(int burnDuration) {
        this.burnDuration = burnDuration;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setAllowPvP(boolean allowPvP) {
        this.entityData.set(ALLOW_PVP, allowPvP);
    }

    public boolean allowsPvP() {
        return this.entityData.get(ALLOW_PVP);
    }

    @Override
    public byte getPierceLevel() {
        return 0;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && (this.entityLocked == null || !this.entityLocked.isAlive())) {
            this.discard();
            return;
        }

        if (this.entityLocked != null) {
            Vec3 target = this.entityLocked.position().add(0.0, this.entityLocked.getBbHeight() * 0.5, 0.0);
            Vec3 direction = target.subtract(this.position());

            if (direction.lengthSqr() <= 1.0) {
                this.hitTarget();
                return;
            }

            this.setDeltaMovement(direction.normalize().scale(HOMING_SPEED));
        }

        super.tick();

        if (this.tickCount > 100) {
            this.discard();
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!(entity instanceof LivingEntity) || entity == this.getOwner()) {
            return false;
        }

        if (entity instanceof Player player && this.getOwner() instanceof Player owner) {
            return this.allowsPvP() && owner.canHarmPlayer(player) && super.canHitEntity(entity);
        }

        return super.canHitEntity(entity);
    }

    @Override
    protected void onHit(HitResult result) {
        if (!this.level.isClientSide) {
            if (result instanceof EntityHitResult entityHitResult
                    && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                this.entityLocked = livingEntity;
            }
            this.hitTarget();
        }
    }

    private void hitTarget() {
        if (this.entityLocked != null && this.entityLocked.isAlive()) {
            LivingEntity owner = this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null;
            DamageSource source = owner instanceof Player player
                    ? DamageSource.playerAttack(player)
                    : owner != null ? DamageSource.mobAttack(owner) : DamageSource.MAGIC;

            if (this.damage > 0.0F) {
                ActiveFlags.IS_AP_ATTACKING.runIfNotSet(() -> this.entityLocked.hurt(source, this.damage));
            }

            if (this.burnDuration > 0) {
                PercentBurnEffect.applyBurnEffect(
                        this.entityLocked,
                        this.burnDuration,
                        this.amplifier,
                        this.damage,
                        owner
                );
            }
        }

        this.discard();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.burnDuration = tag.getInt("BurnDuration");
        this.amplifier = tag.getInt("Amplifier");
        this.damage = tag.getFloat("MeteorDamage");
        this.setAllowPvP(tag.getBoolean("AllowPvP"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BurnDuration", this.burnDuration);
        tag.putInt("Amplifier", this.amplifier);
        tag.putFloat("MeteorDamage", this.damage);
        tag.putBoolean("AllowPvP", this.allowsPvP());
    }

    @Override
    protected boolean tryPickup(Player player) {
        return false;
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
