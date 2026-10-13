package xyz.iwolfking.woldsvaults.entities.msc;

import iskallia.vault.entity.entity.*;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.calc.AbilityPowerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import xyz.iwolfking.woldsvaults.init.ModEntities;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public class VaultMeteorStormEntity extends Entity {
    private int duration;
    private float radius;
    private float percentAbilityPowerDealt;
    private int intervalTicks;
    private int intervalTicksMax;

    private int burnDuration;
    private int amplifier;
    private double damagePerMeteor;
    private float percentAbilityPowerDealtMin;
    private float percentAbilityPowerDealtMax;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    private static final EntityDataAccessor<Integer> ID_TYPE = SynchedEntityData.defineId(VaultMeteorStormEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(VaultMeteorStormEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ALLOW_PVP = SynchedEntityData.defineId(VaultMeteorStormEntity.class, EntityDataSerializers.BOOLEAN);

    private static Predicate<Entity> createEntityPredicate(boolean allowPlayers, @Nullable Entity owner) {
        return entity ->
                (allowPlayers || !(entity instanceof Player))
                        && (entity instanceof LivingEntity livingEntity)
                        && livingEntity.isAlive()
                        && (owner == null || !entity.getUUID().equals(owner.getUUID()));
    }

    public VaultMeteorStormEntity(EntityType<? extends VaultMeteorStormEntity> type, Level world){
        super(type, world);
        this.setInvulnerable(true);
        this.radius = 5;
        this.intervalTicks = 0;
        this.intervalTicksMax = 10;
    }

    public VaultMeteorStormEntity(Level world, double x, double y, double z, float radius, int duration,
                                  Entity owner, float minPercent, float maxPercent, int intervalTicks) {
        this(ModEntities.STORM, world);
        this.setPos(x, y, z);
        this.setYRot((float)(this.random.nextDouble() * 360.0D));

        this.intervalTicks = 0;
        this.intervalTicksMax = intervalTicks;
        this.duration = duration;
        this.radius = radius;
        this.entityData.set(RADIUS, radius);
        this.percentAbilityPowerDealtMin = minPercent;
        this.percentAbilityPowerDealtMax = maxPercent;
        setOwner(owner);
    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            this.cachedOwner = ((ServerLevel)this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    public void setOwner(@Nullable Entity pEntity) {
        if (pEntity != null) {
            this.ownerUUID = pEntity.getUUID();
            this.cachedOwner = pEntity;
        }
    }

    public void setBurnDuration(int burnDuration) {
        this.burnDuration = burnDuration;
    }

    private int getBurnDuration() {
        return this.burnDuration;
    }

    public void setMeteorDamage(float damagePerMeteor) {
        this.damagePerMeteor = damagePerMeteor;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }


    private int getAmplifier() {
        return amplifier;
    }

    public void setMeteorType(VaultMeteorArrow.MeteorType type) {
        this.entityData.set(ID_TYPE, type.ordinal());
    }

    public VaultMeteorArrow.MeteorType getMeteorArrowType() {
        return VaultMeteorArrow.MeteorType.byId(this.entityData.get(ID_TYPE));
    }

    public int getDuration() {
        return this.duration;
    }

    public void setAllowPvP(boolean allow) {
        this.getEntityData().set(ALLOW_PVP, allow);
    }

    public boolean allowsPvP() {
        return this.getEntityData().get(ALLOW_PVP);
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.AMBIENT;
    }

    public static Vec3 generatePointInCircle(Vec3 center, double radius, double time, Random random, boolean below) {

        double angle = 2 * Math.PI * random.nextDouble();
        double distance = Math.sqrt(random.nextDouble());

        // Adjust the cloud shape by adding multiple sinusoidal factors to the radius
        double cloudRadius = radius + Math.sin(3 * angle + time) * radius * 0.13
                + Math.sin(4 * (angle + Math.sin(time)) + time * 2) * radius * 0.13
                + Math.sin(5 * (angle + Math.sin(time)) + time * 3) * radius * 0.13;

        // Calculate the height based on the distance from the center to the full radius
        double height = (((1.0 - distance * distance * distance)) / radius) * cloudRadius * 0.75;

        if(!below){
            if (random.nextBoolean()) {
                height *= -1; // Randomly negate the height
            }
        } else {
            height *= -1;
        }

        double x = center.x() + Math.cos(angle) * cloudRadius * distance;
        double y = center.y() + height;
        double z = center.z() + Math.sin(angle) * cloudRadius * distance;

        return new Vec3(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        VaultMeteorArrow.MeteorType meteorType = this.getMeteorArrowType();
        if (this.level.isClientSide()) {
            for (int i = 0; i < 6 * this.entityData.get(RADIUS); i++) {
                Vec3 vec3 = generatePointInCircle(this.position(), this.entityData.get(RADIUS) + 0.5f, tickCount / 50f, random, false);

                ParticleEngine pm = Minecraft.getInstance().particleEngine;
                Particle particle = pm.createParticle(ModParticles.STORM_CLOUD.get(), vec3.x(), vec3.y() + random.nextDouble() * 0.4f, vec3.z(), 0, 0, 0);

                if (meteorType == VaultMeteorArrow.MeteorType.BASE) {
                    if (particle != null) {
                        float f = this.random.nextFloat() * 0.1F;
                        particle.setColor(0.6f + f, 0.7f + f, 0.9f + f);
                    }
                }
            }

            // Duration and the damage-related fields are server-side state. The
            // server will remove this entity and synchronize that removal to the
            // client, so the client should only generate the cloud particles.
            return;
        }
        if (this.intervalTicks > 0) {
            this.intervalTicks--;
        } else {
            if (getOwner() instanceof Player player) {
                if (meteorType == VaultMeteorArrow.MeteorType.BASE) {
                    ArrayList<LivingEntity> result = new ArrayList<>();
                    getEntitiesInRange(player.level, this.position(), radius, createEntityPredicate(this.allowsPvP(), player), result, 14);

                    result.removeIf(entity -> entity instanceof EternalEntity || entity instanceof PetEntity);
                    if (!result.isEmpty()) {
                        LivingEntity livingEntity = result.get(player.level.random.nextInt(result.size()));
                        VaultMeteor meteor = ModEntities.VAULT_METEOR.create(player.level);

                        if (meteor != null) {
                            Vec3 pos = livingEntity.position();
                            pos = new Vec3(pos.x, this.getY(), pos.z);
                            meteor.moveTo(pos);
                            meteor.setOwner(getOwner());
                            meteor.setAllowPvP(this.allowsPvP());
                            meteor.setEntityLocked(livingEntity);
                            meteor.setAmplifier(this.getAmplifier());
                            meteor.setBurnDuration(this.getBurnDuration());
                            float abilityPowerPercent = JavaRandom.ofNanoTime().nextFloat(
                                    this.percentAbilityPowerDealtMin,
                                    this.percentAbilityPowerDealtMax
                            );
                            meteor.setDamage(AbilityPowerHelper.getAbilityPower(player) * abilityPowerPercent);
                            player.level.addFreshEntity(meteor);
                            player.level.playSound(
                                    null,
                                    meteor.getX(), meteor.getY(), meteor.getZ(),
                                    SoundEvents.FIRECHARGE_USE,
                                    SoundSource.PLAYERS,
                                    1.0F,
                                    0.85F + this.random.nextFloat() * 0.3F
                            );
                        }
                    }
                }
                this.intervalTicks = this.intervalTicksMax;
            }

        }
        if (--this.duration < 0) {
            this.discard();
        }
    }

    public static void getEntitiesInRange(LevelAccessor levelAccessor, Vec3 center, float range, Predicate<Entity> filter, List<LivingEntity> result, float height) {

        // We add 4 to the range of the AABB to select entities that might be
        // outside the range, but still intersecting with the sphere
        getEntitiesInRange(levelAccessor, new AABB(
                        center.x - (range + 4), center.y - (height + 4), center.z - (range + 4),
                        center.x + (range + 4), center.y + (height + 4), center.z + (range + 4)),
                center, range, filter, result, height);
    }

    public static void getEntitiesInRange(LevelAccessor levelAccessor, AABB area, Vec3 center, float range, Predicate<Entity> filter, List<LivingEntity> result, float height) {
        if (levelAccessor == null) {
            return;
        }

        List<Entity> entityList = levelAccessor.getEntities((Entity) null, area, filter);

        for (Entity entity : entityList) {

            if (isAABBIntersectingOrInsideCylinder(entity, center.x, center.y, center.z, range, height)) {
                result.add((LivingEntity) entity);
            }
        }
    }

    public static double getDistanceInXZ(Vec3 point1, Vec3 point2) {
        double deltaX = point2.x() - point1.x();
        double deltaZ = point2.z() - point1.z();
        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    public static boolean isAABBIntersectingOrInsideCylinder(Entity entity, double xCylinderCenter, double yCylinderCenter, double zCylinderCenter, double cylinderRadius, double cylinderHeight) {
        // Calculate the top and bottom positions of the cylinder
        if(entity.position().y + entity.getBbHeight() > yCylinderCenter) return false;
        if(entity.position().y < yCylinderCenter - cylinderHeight) return false;

        return !(getDistanceInXZ(entity.position(), new Vec3(xCylinderCenter, yCylinderCenter, zCylinderCenter)) > cylinderRadius);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ID_TYPE, 0);
        this.entityData.define(RADIUS, 5.0F);
        this.entityData.define(ALLOW_PVP, false);
    }

    @Override
    protected void doWaterSplashEffect() {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("Duration", this.duration);
        pCompound.putFloat("Radius", this.radius);
        pCompound.putFloat("PercentAbilityPowerDealt", this.percentAbilityPowerDealt);
        pCompound.putInt("IntervalTicks", this.intervalTicks);
        pCompound.putInt("IntervalTicksMax", this.intervalTicksMax);
        pCompound.putInt("Type", this.entityData.get(ID_TYPE));
        pCompound.putInt("burnDuration", this.burnDuration);
        pCompound.putInt("amplifier", this.amplifier);
        pCompound.putDouble("damagePerMeteor", this.damagePerMeteor);
        pCompound.putFloat("PercentAbilityPowerDealtMin", this.percentAbilityPowerDealtMin);
        pCompound.putFloat("PercentAbilityPowerDealtMax", this.percentAbilityPowerDealtMax);
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.duration = pCompound.getInt("Duration");
        this.radius = pCompound.getFloat("Radius");
        this.percentAbilityPowerDealt = pCompound.getFloat("PercentAbilityPowerDealt");
        this.intervalTicks = pCompound.getInt("IntervalTicks");
        this.intervalTicksMax = pCompound.getInt("IntervalTicksMax");
        this.entityData.set(ID_TYPE, pCompound.getInt("Type"));
        if(pCompound.contains("burnDuration"))
            this.burnDuration = pCompound.getInt("burnDuration");
        if(pCompound.contains("amplifier"))
            this.amplifier = pCompound.getInt("amplifier");
        if(pCompound.contains("damagePerMeteor"))
            this.damagePerMeteor = pCompound.getDouble("damagePerMeteor");
        if (pCompound.contains("PercentAbilityPowerDealtMin"))
            this.percentAbilityPowerDealtMin = pCompound.getFloat("PercentAbilityPowerDealtMin");
        if (pCompound.contains("PercentAbilityPowerDealtMax"))
            this.percentAbilityPowerDealtMax = pCompound.getFloat("PercentAbilityPowerDealtMax");
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        Entity entity = this.level.getEntity(pPacket.getData());
        if (entity != null) {
            this.setOwner(entity);
        }
    }
}
