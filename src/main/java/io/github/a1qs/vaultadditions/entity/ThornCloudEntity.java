package io.github.a1qs.vaultadditions.entity;

import io.github.a1qs.vaultadditions.init.ModEntities;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.init.ModParticles;
import iskallia.vault.world.data.VaultPartyData;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class ThornCloudEntity extends Entity {
    private static final EntityDataAccessor<Float> RADIUS  = SynchedEntityData.defineId(ThornCloudEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IGNORE_RADIUS = SynchedEntityData.defineId(ThornCloudEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(ThornCloudEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DAMAGE_DELAY = SynchedEntityData.defineId(ThornCloudEntity.class, EntityDataSerializers.INT);

    private int duration; // Duration of the Cloud
    private int waitTime; // Time to wait until being effective
    private LivingEntity owner; // Owner of the Cloud
    private UUID ownerUniqueId; // UUID of the Owner

    public ThornCloudEntity(EntityType<? extends ThornCloudEntity> cloud, Level world) {
        super(cloud, world);
        this.duration = 600;
        this.waitTime = 20;
        this.noPhysics = true;
        this.setRadius(3.0F);
    }

    public ThornCloudEntity(Level world, double x, double y, double z) {
        this(ModEntities.THORN_CLOUD, world);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(RADIUS, 0.5F);
        this.getEntityData().define(IGNORE_RADIUS, false);
        this.getEntityData().define(DAMAGE, 1.0F);
        this.getEntityData().define(DAMAGE_DELAY, 10); // Default value for damageDelay
    }

    public float getRadius() {
        return this.getEntityData().get(RADIUS);
    }

    public void setRadius(float radiusIn) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(RADIUS, radiusIn);
        }
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    protected void setIgnoreRadius(boolean ignoreRadius) {
        this.getEntityData().set(IGNORE_RADIUS, ignoreRadius);
    }

    public boolean shouldIgnoreRadius() {
        return this.getEntityData().get(IGNORE_RADIUS);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int durationIn) {
        this.duration = durationIn;
    }

    public int getDamageDelay() {
        return this.getEntityData().get(DAMAGE_DELAY);
    }

    public void setDamageDelay(int damageDelay) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DAMAGE_DELAY, damageDelay);
        }
    }

    public void setDamage(float damage) {
        this.getEntityData().set(DAMAGE, damage);
    }

    public float getDamage() {
        return this.getEntityData().get(DAMAGE);
    }


    public void setRemainingFireTicks(int seconds) {
        super.setRemainingFireTicks(0);
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            this.tickParticles();
        } else {
            boolean ignoreRadius = this.shouldIgnoreRadius();
            float radius = this.getRadius();

            if (this.tickCount >= this.waitTime + this.duration) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }

            boolean flag1 = this.tickCount < this.waitTime;
            if (ignoreRadius != flag1) {
                this.setIgnoreRadius(flag1);
            }

            if (flag1) {
                return;
            }

            if (this.tickCount % this.getDamageDelay() == 0) {
                List<LivingEntity> entitiesInRadius = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius));

                for (LivingEntity entity : entitiesInRadius) {
                    if (canApplyEffects(entity)) {
                        entity.hurt(DamageSource.thorns(this), this.getDamage());
                        entity.invulnerableTime = 0;
                        entity.setDeltaMovement(0, 0, 0);

                        level.playSound(null, entity.blockPosition(), SoundEvents.THORNS_HIT, SoundSource.PLAYERS, 0.4F, 1.0F);
                    }
                }
            }
        }
    }

    private int summonTimer = 0; // Timer to track particle summon phase

    private void tickParticles() {
        var boundingBox = this.getBoundingBox();
        double centerX = (boundingBox.minX + boundingBox.maxX) / 2.0;
        double centerZ = (boundingBox.minZ + boundingBox.maxZ) / 2.0;
        double staticY = (boundingBox.minY + boundingBox.maxY) / 2.0;
        double radius = this.getRadius() * 2.2;
        int particleCount = (int) (2 * radius * 0.5);

        ParticleEngine pm = Minecraft.getInstance().particleEngine;

        // Add vertical motion
        double oscillationY = 0.5 * Math.sin(summonTimer * 0.2);
        double currentY = staticY + oscillationY;


        for (int i = 0; i < particleCount; i++) {
            double angle = (2 * Math.PI * i / particleCount) + (summonTimer * 0.1);
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);
            double shakeX = (Math.random() - 0.5) * 0.1;
            double shakeZ = (Math.random() - 0.5) * 0.1;

            Particle particle = pm.createParticle(ModParticles.NOVA_SPEED.get(), x + shakeX, currentY, z + shakeZ, 0.0D, 0.0D, 0.0D);
            if (particle != null) {
                particle.setColor(0.1F, 0.9F, 0.3F);
                particle.scale(2.0F);
            }



            for (int trail = 1; trail < 3; trail++) {
                if(summonTimer % this.getDamageDelay() == 0 && summonTimer >= waitTime) {
                    double trailProgress = (double) trail / 3.0;
                    double trailX = centerX + (x - centerX) * trailProgress;
                    double trailZ = centerZ + (z - centerZ) * trailProgress;
                    Particle trailParticle = pm.createParticle(ParticleTypes.SWEEP_ATTACK, trailX, currentY, trailZ, 0.0D, 0.0D, 0.0D);
                    if (trailParticle != null) {
                        float mix = (float) trail / 3.0F;

                        float red = 23 / 255.0F;
                        float green = 145 / 255.0F;
                        float blue = 2 / 255.0F;

                        trailParticle.setColor(red, green, blue);
                        trailParticle.scale(0.6F + mix);
                    }
                }
            }
        }

        if (summonTimer % 20 == 0) {
            for (int i = 0; i < 5; i++) {
                double burstAngle = 2 * Math.PI * i / 5;
                double burstX = centerX + Math.cos(burstAngle) * (radius + 0.5);
                double burstZ = centerZ + Math.sin(burstAngle) * (radius + 0.5);
                Particle burstParticle = pm.createParticle(ModParticles.UBER_PYLON.get(), burstX, currentY, burstZ, Math.cos(burstAngle) * 0.2, 0.1, Math.sin(burstAngle) * 0.2);
                if (burstParticle != null) burstParticle.scale(5.0F);
            }
        }

        int sphereParticleCount = 10; // More particles for sphere
        for (int i = 0; i < sphereParticleCount; i++) {
            // Spherical coordinates
            double theta = Math.random() * 2 * Math.PI; // Random horizontal angle
            double phi = Math.random() * Math.PI;      // Random vertical angle
            double sphereX = centerX + radius * Math.sin(phi) * Math.cos(theta);
            double sphereY = staticY + radius * Math.cos(phi); // Static Y-level with vertical offset
            double sphereZ = centerZ + radius * Math.sin(phi) * Math.sin(theta);

            // Create particles to define the sphere
            Particle sphereParticle = pm.createParticle(ModParticles.UBER_PYLON.get(), sphereX, sphereY, sphereZ, 0.0D, 0.0D, 0.0D);
            if (sphereParticle != null) {
                sphereParticle.scale(1.5F);
                sphereParticle.setLifetime(30);
            }
        }

        summonTimer++;
    }




    protected boolean canApplyEffects(LivingEntity target) {
        if (this.ownerUniqueId == null) {
            return true;
        }

        UUID targetUUID = target.getUUID();
        if (targetUUID.equals(this.ownerUniqueId)) {
            return false;
        }

        UUID ownerUUID = this.ownerUniqueId;
        Level world = this.getCommandSenderWorld();
        if (!(world instanceof ServerLevel serverLevel)) {
            return true;
        }

        if (target instanceof EternalEntity) {
            UUID eternalTargetOwnerUUID = ((EternalEntity)target).getOwner().map(Function.identity(), Entity::getUUID);
            if (eternalTargetOwnerUUID.equals(ownerUUID)) {
                return false;
            }

            VaultPartyData.Party party = VaultPartyData.get(serverLevel).getParty(eternalTargetOwnerUUID).orElse(null);
            if (party != null && party.hasMember(ownerUUID)) {
                return false;
            }
        }

        if (target instanceof TamableAnimal tamable) {
            if (ownerUUID.equals(tamable.getOwnerUUID())) {
                return false;
            }
        }

        VaultPartyData.Party party = VaultPartyData.get(serverLevel).getParty(ownerUUID).orElse(null);
        return party == null || !party.hasMember(targetUUID);
    }

    public void setWaitTime(int waitTimeIn) {
        this.waitTime = waitTimeIn;
    }

    public void setOwner(@Nullable LivingEntity ownerIn) {
        this.owner = ownerIn;
        this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUniqueId != null && this.level instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUniqueId);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.tickCount = compound.getInt("Age");
        this.duration = compound.getInt("Duration");
        this.waitTime = compound.getInt("WaitTime");
        this.setRadius(compound.getFloat("Radius"));
        if (compound.hasUUID("Owner")) {
            this.ownerUniqueId = compound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Age", this.tickCount);
        compound.putInt("Duration", this.duration);
        compound.putInt("WaitTime", this.waitTime);
        compound.putFloat("Radius", this.getRadius());

        if (this.ownerUniqueId != null) {
            compound.putUUID("Owner", this.ownerUniqueId);
        }

    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        if (RADIUS.equals(key)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(key);
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose poseIn) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return false;
    }
}

