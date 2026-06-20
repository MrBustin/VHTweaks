package io.github.a1qs.vaultadditions.entity;

import io.github.a1qs.vaultadditions.init.ModEntities;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class IdonasBarrageArrowEntity extends AbstractArrow {
    private float radius;
    private float durationTicks;
    private float damageTickFrequency;
    private float damageMultiplier;

    public IdonasBarrageArrowEntity(EntityType<? extends IdonasBarrageArrowEntity> entityType, Level level) {
        super(entityType, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public IdonasBarrageArrowEntity(Level level, LivingEntity shooter) {
        super(ModEntities.IDONAS_BARRAGE_ARROW, shooter, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public void configure(float radius, float durationTicks, float damageTickFrequency, float damageMultiplier) {
        this.radius = radius;
        this.durationTicks = durationTicks;
        this.damageTickFrequency = damageTickFrequency;
        this.damageMultiplier = damageMultiplier;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level.isClientSide && !this.inGround) {
            Vec3 movement = this.getDeltaMovement();
            for (int i = 0; i < 8; i++) {
                double offset = i / 8.0D;
                this.level.addParticle(DustParticleOptions.REDSTONE,
                        this.getX() - movement.x * offset,
                        this.getY() - movement.y * offset,
                        this.getZ() - movement.z * offset,
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (!this.level.isClientSide) {
            Vec3 location = result.getLocation();
            IdonasBarrageEntity barrage = new IdonasBarrageEntity(this.level, location.x, location.y, location.z);
            Entity owner = this.getOwner();
            barrage.setOwner(owner instanceof LivingEntity livingEntity ? livingEntity : null);
            barrage.setRadius(this.radius);
            barrage.setDurationTicks(this.durationTicks);
            barrage.setDamageTickFrequency(this.damageTickFrequency);
            barrage.setDamageMultiplier(this.damageMultiplier);
            if (owner != null) {
                barrage.setShotDirection((float)(location.x - owner.getX()), (float)(location.z - owner.getZ()));
            } else {
                Vec3 movement = this.getDeltaMovement();
                barrage.setShotDirection((float)movement.x, (float)movement.z);
            }
            this.level.addFreshEntity(barrage);
        }

        this.discard();
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Radius", this.radius);
        tag.putFloat("DurationTicks", this.durationTicks);
        tag.putFloat("DamageTickFrequency", this.damageTickFrequency);
        tag.putFloat("DamageMultiplier", this.damageMultiplier);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.radius = tag.getFloat("Radius");
        this.durationTicks = tag.getFloat("DurationTicks");
        this.damageTickFrequency = tag.getFloat("DamageTickFrequency");
        this.damageMultiplier = tag.getFloat("DamageMultiplier");
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
