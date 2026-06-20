package io.github.a1qs.vaultadditions.entity;

import io.github.a1qs.vaultadditions.init.ModEntities;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.world.data.VaultPartyData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class IdonasBarrageEntity extends Entity {
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(IdonasBarrageEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DURATION_TICKS = SynchedEntityData.defineId(IdonasBarrageEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DAMAGE_TICK_FREQUENCY = SynchedEntityData.defineId(IdonasBarrageEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DAMAGE_MULTIPLIER = SynchedEntityData.defineId(IdonasBarrageEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SHOT_DIRECTION_X = SynchedEntityData.defineId(IdonasBarrageEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SHOT_DIRECTION_Z = SynchedEntityData.defineId(IdonasBarrageEntity.class, EntityDataSerializers.FLOAT);

    private LivingEntity owner;
    private UUID ownerUniqueId;
    private int[] volleySoundTicks;
    private int volleySoundIndex;

    public IdonasBarrageEntity(EntityType<? extends IdonasBarrageEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public IdonasBarrageEntity(Level level, double x, double y, double z) {
        this(ModEntities.IDONAS_BARRAGE, level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RADIUS, 4.0F);
        this.entityData.define(DURATION_TICKS, 100.0F);
        this.entityData.define(DAMAGE_TICK_FREQUENCY, 2.0F);
        this.entityData.define(DAMAGE_MULTIPLIER, 1.0F);
        this.entityData.define(SHOT_DIRECTION_X, 0.0F);
        this.entityData.define(SHOT_DIRECTION_Z, 1.0F);
    }

    public float getRadius() {
        return this.entityData.get(RADIUS);
    }

    public void setRadius(float radius) {
        this.entityData.set(RADIUS, radius);
    }

    public float getDurationTicks() {
        return this.entityData.get(DURATION_TICKS);
    }

    public void setDurationTicks(float durationTicks) {
        this.entityData.set(DURATION_TICKS, durationTicks);
    }

    public float getDamageTickFrequency() {
        return this.entityData.get(DAMAGE_TICK_FREQUENCY);
    }

    public void setDamageTickFrequency(float damageTickFrequency) {
        this.entityData.set(DAMAGE_TICK_FREQUENCY, damageTickFrequency);
    }

    public float getDamageMultiplier() {
        return this.entityData.get(DAMAGE_MULTIPLIER);
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.entityData.set(DAMAGE_MULTIPLIER, damageMultiplier);
    }

    public float getShotDirectionX() {
        return this.entityData.get(SHOT_DIRECTION_X);
    }

    public float getShotDirectionZ() {
        return this.entityData.get(SHOT_DIRECTION_Z);
    }

    public void setShotDirection(float x, float z) {
        float length = Mth.sqrt(x * x + z * z);
        if (length < 1.0E-4F) {
            this.entityData.set(SHOT_DIRECTION_X, 0.0F);
            this.entityData.set(SHOT_DIRECTION_Z, 1.0F);
        } else {
            this.entityData.set(SHOT_DIRECTION_X, x / length);
            this.entityData.set(SHOT_DIRECTION_Z, z / length);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount >= Math.max(1, Math.round(this.getDurationTicks()))) {
            this.discard();
            return;
        }

        if (!this.level.isClientSide) {
            this.playScheduledVolleySounds();
            if (this.tickCount % this.getTicksBetweenDamage() == 0) {
                this.damageOneTarget();
            }
        }
    }

    private void playScheduledVolleySounds() {
        if (this.volleySoundTicks == null) {
            this.volleySoundTicks = this.createVolleySoundSchedule();
        }

        while (this.volleySoundIndex < this.volleySoundTicks.length && this.tickCount >= this.volleySoundTicks[this.volleySoundIndex]) {
            this.playVolleySound();
            this.volleySoundIndex++;
        }
    }

    private int[] createVolleySoundSchedule() {
        ArrayList<Integer> schedule = new ArrayList<>();
        int duration = Math.max(1, Math.round(this.getDurationTicks()));
        int volleyStart = 1 + this.level.random.nextInt(2);

        while (volleyStart < duration) {
            int tick = volleyStart;

            for (int i = 0; i < 4; i++) {
                schedule.add(tick);
                tick += 1 + this.level.random.nextInt(3);
            }

            tick += 6 + this.level.random.nextInt(5);
            for (int i = 0; i < 2; i++) {
                schedule.add(tick);
                tick += 1 + this.level.random.nextInt(3);
            }

            tick += 5;
            for (int i = 0; i < 2; i++) {
                schedule.add(tick);
                tick += 1 + this.level.random.nextInt(2);
            }

            volleyStart += 24 + this.level.random.nextInt(7);
        }

        return schedule.stream().mapToInt(Integer::intValue).toArray();
    }

    private void playVolleySound() {
        float radius = Math.max(1.0F, this.getRadius());
        float angle = this.level.random.nextFloat() * Mth.TWO_PI;
        float distance = Mth.sqrt(this.level.random.nextFloat()) * radius;
        double x = this.getX() + Mth.cos(angle) * distance;
        double z = this.getZ() + Mth.sin(angle) * distance;
        float volume = 0.6F + this.level.random.nextFloat() * 0.25F;
        float pitch = 0.85F + this.level.random.nextFloat() * 0.25F;

        this.level.playSound(null, x, this.getY() + 8.0D, z, SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, volume, pitch);
    }

    private int getTicksBetweenDamage() {
        float intervalSeconds = Math.max(0.05F, this.getDamageTickFrequency());
        return Math.max(1, Math.round(intervalSeconds * 20.0F));
    }

    private void damageOneTarget() {
        LivingEntity owner = this.getOwner();
        if (!(owner instanceof Player player)) {
            return;
        }

        DamageSource damageSource = DamageSource.playerAttack(player);
        List<LivingEntity> targets = this.level.getEntitiesOfClass(LivingEntity.class, this.getSearchBox(), this::canDamageTarget);
        targets.removeIf(target -> target.isInvulnerableTo(damageSource) || this.distanceToTargetSqr(target) > this.getRadius() * this.getRadius());

        if (targets.isEmpty()) {
            return;
        }

        LivingEntity target = targets.get(this.level.random.nextInt(targets.size()));
        float damage = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE) * this.getDamageMultiplier();

        ActiveFlags.IS_AOE_ATTACKING.runIfNotSet(() -> {
            if (target.hurt(damageSource, damage)) {
                target.invulnerableTime = 0;
                this.level.playSound(null, target.blockPosition(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.65F, 1.2F);
            }
        });
    }

    private AABB getSearchBox() {
        float radius = this.getRadius();
        return new AABB(this.getX() - radius - 1.0D, this.getY() - 16.0D, this.getZ() - radius - 1.0D,
                this.getX() + radius + 1.0D, this.getY() + 32.0D, this.getZ() + radius + 1.0D);
    }

    private double distanceToTargetSqr(LivingEntity entity) {
        AABB box = entity.getBoundingBox();
        double closestX = Mth.clamp(this.getX(), box.minX, box.maxX);
        double closestZ = Mth.clamp(this.getZ(), box.minZ, box.maxZ);
        double dx = closestX - this.getX();
        double dz = closestZ - this.getZ();
        return dx * dx + dz * dz;
    }

    private boolean canDamageTarget(LivingEntity target) {
        if (target instanceof Player) {
            return false;
        }

        if (this.ownerUniqueId == null) {
            return true;
        }

        UUID targetUUID = target.getUUID();
        if (targetUUID.equals(this.ownerUniqueId)) {
            return false;
        }

        if (!(this.level instanceof ServerLevel serverLevel)) {
            return true;
        }

        if (target instanceof EternalEntity eternal) {
            UUID eternalOwnerUUID = eternal.getOwner().map(Function.identity(), Entity::getUUID);
            if (eternalOwnerUUID.equals(this.ownerUniqueId)) {
                return false;
            }

            VaultPartyData.Party party = VaultPartyData.get(serverLevel).getParty(eternalOwnerUUID).orElse(null);
            if (party != null && party.hasMember(this.ownerUniqueId)) {
                return false;
            }
        }

        if (target instanceof TamableAnimal tamable && this.ownerUniqueId.equals(tamable.getOwnerUUID())) {
            return false;
        }

        VaultPartyData.Party party = VaultPartyData.get(serverLevel).getParty(this.ownerUniqueId).orElse(null);
        return party == null || !party.hasMember(targetUUID);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUniqueId = owner == null ? null : owner.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUniqueId != null && this.level instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.ownerUniqueId);
            if (entity instanceof LivingEntity livingEntity) {
                this.owner = livingEntity;
            }
        }

        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.tickCount = tag.getInt("Age");
        this.setRadius(tag.getFloat("Radius"));
        this.setDurationTicks(tag.getFloat("DurationTicks"));
        this.setDamageTickFrequency(tag.getFloat("DamageTickFrequency"));
        this.setDamageMultiplier(tag.getFloat("DamageMultiplier"));
        this.setShotDirection(tag.getFloat("ShotDirectionX"), tag.getFloat("ShotDirectionZ"));
        if (tag.hasUUID("Owner")) {
            this.ownerUniqueId = tag.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("Age", this.tickCount);
        tag.putFloat("Radius", this.getRadius());
        tag.putFloat("DurationTicks", this.getDurationTicks());
        tag.putFloat("DamageTickFrequency", this.getDamageTickFrequency());
        tag.putFloat("DamageMultiplier", this.getDamageMultiplier());
        tag.putFloat("ShotDirectionX", this.getShotDirectionX());
        tag.putFloat("ShotDirectionZ", this.getShotDirectionZ());
        if (this.ownerUniqueId != null) {
            tag.putUUID("Owner", this.ownerUniqueId);
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
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        float radius = this.getRadius();
        return EntityDimensions.scalable(radius * 2.0F, 0.5F);
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
