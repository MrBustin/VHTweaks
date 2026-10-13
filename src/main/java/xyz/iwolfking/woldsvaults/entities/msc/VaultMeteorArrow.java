package xyz.iwolfking.woldsvaults.entities.msc;

import iskallia.vault.entity.IPlayerAttackIgnore;
import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.entity.entity.PetEntity;
import iskallia.vault.entity.entity.VaultStormArrow;
import iskallia.vault.entity.entity.VaultStormEntity;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import xyz.iwolfking.woldsvaults.init.ModEntities;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class VaultMeteorArrow extends AbstractArrow {
    private static final EntityDataAccessor<Float> ID_DAMAGE = SynchedEntityData.defineId(VaultStormArrow.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> THROWER_UUID = SynchedEntityData.defineId(VaultStormArrow.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> ID_TYPE = SynchedEntityData.defineId(VaultStormArrow.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ALLOW_PVP = SynchedEntityData.defineId(VaultStormArrow.class, EntityDataSerializers.BOOLEAN);
    private boolean grounded;
    private int life;
    public Vec3 prevDeltaMovement = new Vec3((double)0.0F, (double)0.0F, (double)0.0F);
    private float radius;
    private int duration;
    private int intervalTicks;
    private float percentAbilityPowerDealt;
    private boolean leftOwner;
    private boolean hasBeenShot;
    private double damagePerMeteor;
    private int burnDuration;
    private int amplifier;
    private float percentAbilityPowerDealtMin;
    private float percentAbilityPowerDealtMax;


    public VaultMeteorArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public VaultMeteorArrow(Level level, LivingEntity thrower) {
        super(ModEntities.METEOR_ARROW, thrower, level);
        this.setOwner(thrower);
        this.entityData.set(THROWER_UUID, Optional.of(thrower.getUUID()));
    }

    public void setBurnDuration(int burnDuration){
        this.burnDuration = burnDuration;
    }

    public void setAmplifier(int amplifier){
        this.amplifier = amplifier;
    }

    public void setMeteorStormDamage(double damagePerMeteor){
        this.damagePerMeteor = damagePerMeteor;
    }

    public void setMeteorStormDamage(double damagePerMeteor, float percentAbilityPowerDealt) {
        this.damagePerMeteor = damagePerMeteor;
        this.percentAbilityPowerDealt = percentAbilityPowerDealt;
    }


    private int getBurnDuration() {
        return this.burnDuration;
    }

    private int getAmplifier() {
        return this.amplifier;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setAllowPvP(boolean allow) {
        this.entityData.set(ALLOW_PVP, allow);
    }

    public boolean allowsPvP() {
        return (Boolean)this.entityData.get(ALLOW_PVP);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setIntervalTicks(int intervalTicks) {
        this.intervalTicks = intervalTicks;
    }

    public void setAbilityPowerPercent(float percentAbilityPowerDealt) {
        this.percentAbilityPowerDealt = percentAbilityPowerDealt;
    }

    public void setAbilityPowerPercentRange(float min, float max) {
        this.percentAbilityPowerDealtMin = min;
        this.percentAbilityPowerDealtMax = max;
    }

    public void setMeteorArrowType(int id){
        this.setMeteorArrowType(VaultMeteorArrow.MeteorType.byId(id));
    }

    public void setMeteorArrowType(MeteorType type){
        this.entityData.set(ID_TYPE, type.ordinal());
    }

    public MeteorType getMeteorArrowType() {
        return VaultMeteorArrow.MeteorType.byId((Integer) this.entityData.get(ID_TYPE));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_DAMAGE, 0.0F);
        this.entityData.define(ID_TYPE, 0);
        this.entityData.define(THROWER_UUID, Optional.empty());
        this.entityData.define(ALLOW_PVP, false);
    }

    public byte getPierceLevel() {
        return 0;
    }

    private boolean shouldFall() {
        return this.inGround && this.level.noCollision((new AABB(this.position(), this.position())).inflate(0.06));
    }

    private void startFalling() {
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.life = 0;
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();
        if (entity != null) {
            for(Entity entity1 : this.level.getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate((double)1.0F), (p_37272_) -> !p_37272_.isSpectator() && p_37272_.isPickable())) {
                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }

        return true;
    }

    public void tick() {
        if (this.inGroundTime > 4) {
            this.grounded = true;
        }

        if (this.level.isClientSide() && !this.grounded && this.tickCount > 2) {
            for(int i = 0; i < 2; ++i) {
                ParticleEngine pm = Minecraft.getInstance().particleEngine;
                float divisor = (float)i + 1.0F;
                Particle particle = pm.createParticle(ModParticles.ENDER_ANCHOR.get(), this.position().x + this.getDeltaMovement().x / (double)divisor, this.position().y + this.getDeltaMovement().y / (double)divisor, this.position().z + this.getDeltaMovement().z / (double)divisor, (double)0.0F, (double)0.0F, (double)0.0F);
                if (particle != null) {
                    if (this.getMeteorArrowType() == VaultMeteorArrow.MeteorType.BASE) {
                        particle.setColor(1.0F, 0.9F, 0.0F);
                    }

                    particle.scale(0.75F);
                }

                particle = pm.createParticle(ModParticles.ENDER_ANCHOR.get(), this.position().x + this.getDeltaMovement().x / (double)divisor, this.position().y + this.getDeltaMovement().y / (double)divisor, this.position().z + this.getDeltaMovement().z / (double)divisor, (double)0.0F, (double)0.0F, (double)0.0F);
                if (particle != null) {
                    if (this.getMeteorArrowType() == VaultMeteorArrow.MeteorType.BASE) {
                        particle.setColor(1.0F, 0.9F, 0.9F);
                    }

                    particle.scale(0.35F);
                }
            }
        }

        this.prevDeltaMovement = this.getDeltaMovement();
        this.baseTick();
        if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner(), this.blockPosition());
            this.hasBeenShot = true;
        }

        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }

        boolean flag = this.isNoPhysics();
        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
            this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        BlockPos blockpos = this.blockPosition();
        BlockState blockstate = this.level.getBlockState(blockpos);
        if (!blockstate.isAir() && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = this.position();

                for(AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW)) {
            this.clearFire();
        }

        if (this.inGround && !flag) {
            if (this.shouldFall()) {
                this.startFalling();
            } else if (!this.level.isClientSide) {
                this.tickDespawn();
            }

            ++this.inGroundTime;
        } else {
            this.inGroundTime = 0;
            Vec3 vec32 = this.position();
            Vec3 vec33 = vec32.add(vec3);
            HitResult hitresult = this.level.clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hitresult.getType() != HitResult.Type.MISS) {
                vec33 = hitresult.getLocation();
            }

            while(!this.isRemoved()) {
                EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
                if (entityhitresult != null) {
                    hitresult = entityhitresult;
                }

                if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult)hitresult).getEntity();
                    Entity entity1 = this.getOwner();
                    boolean allowPvP = this.allowsPvP();
                    if (!allowPvP && entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                        hitresult = null;
                        entityhitresult = null;
                    }
                }

                if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !flag && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                    this.onHit(hitresult);
                    this.hasImpulse = true;
                }

                if (entityhitresult == null || this.getPierceLevel() <= 0) {
                    break;
                }

                hitresult = null;
            }

            vec3 = this.getDeltaMovement();
            double d5 = vec3.x;
            double d6 = vec3.y;
            double d1 = vec3.z;
            if (this.isCritArrow()) {
                for(int i = 0; i < 4; ++i) {
                    this.level.addParticle(ParticleTypes.CRIT, this.getX() + d5 * (double)i / (double)4.0F, this.getY() + d6 * (double)i / (double)4.0F, this.getZ() + d1 * (double)i / (double)4.0F, -d5, -d6 + 0.2, -d1);
                }
            }

            double d7 = this.getX() + d5;
            double d2 = this.getY() + d6;
            double d3 = this.getZ() + d1;
            double d4 = vec3.horizontalDistance();
            if (flag) {
                this.setYRot((float)(Mth.atan2(-d5, -d1) * (double)(180F / (float)Math.PI)));
            } else {
                this.setYRot((float)(Mth.atan2(d5, d1) * (double)(180F / (float)Math.PI)));
            }

            this.setXRot((float)(Mth.atan2(d6, d4) * (double)(180F / (float)Math.PI)));
            this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
            this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            float f = 0.99F;
            float f1 = 0.05F;
            if (this.isInWater()) {
                for(int j = 0; j < 4; ++j) {
                    float f2 = 0.25F;
                    this.level.addParticle(ParticleTypes.BUBBLE, d7 - d5 * (double)0.25F, d2 - d6 * (double)0.25F, d3 - d1 * (double)0.25F, d5, d6, d1);
                }

                f = this.getWaterInertia();
            }

            this.setDeltaMovement(vec3.scale(f));
            if (!this.isNoGravity() && !flag) {
                Vec3 vec34 = this.getDeltaMovement();
                this.setDeltaMovement(vec34.x, vec34.y - (double)0.025F, vec34.z);
            }

            this.setPos(d7, d2, d3);
            this.checkInsideBlocks();
        }

    }

    @NotNull
    protected List<LivingEntity> getTargetEntities(Level world, LivingEntity attacker, Vec3 pos, float radius) {
        AABB aabb = new AABB(pos.x - (double)radius, pos.y - (double)radius, pos.z - (double)radius, pos.x + (double)radius, pos.y + (double)radius, pos.z + (double)radius);
        return world.getEntitiesOfClass(LivingEntity.class, aabb);
    }

    protected void onHit(@NotNull HitResult result) {
        VaultMeteorStormEntity storm = new VaultMeteorStormEntity(this.level, this.position().x, this.position().y + (double)8.0F, this.position().z, this.radius, this.duration, this.getOwner(), this.percentAbilityPowerDealtMin, this.percentAbilityPowerDealtMax, this.intervalTicks);
        storm.setAllowPvP(this.allowsPvP());
        storm.setMeteorType(this.getMeteorArrowType());
        storm.setAmplifier(this.getAmplifier());
        storm.setBurnDuration(this.getBurnDuration());
        storm.setMeteorDamage((float)this.damagePerMeteor);

        this.level.addFreshEntity(storm);
        this.remove(RemovalReason.DISCARDED);
        super.onHit(result);
    }

    protected boolean canHitEntity(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return false;
        } else {
            boolean allowPvP = this.allowsPvP();
            return super.canHitEntity(entity) && (!(entity instanceof Player) || allowPvP) && !(entity instanceof PetEntity) && !(entity instanceof EternalEntity) && !IPlayerAttackIgnore.isIgnoredByPlayerAttack(this.getOwner(), entity);
        }
    }

    public void onInsideBubbleColumn(boolean pDownwards) {
        super.onInsideBubbleColumn(pDownwards);
    }

    protected void handleNetherPortal() {
    }

    public boolean canChangeDimensions() {
        return false;
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return this.grounded ? null : super.findHitEntity(pStartVec, pEndVec);
    }

    public Packet<?> getAddEntityPacket() {
        return super.getAddEntityPacket();
    }

    protected void onHitEntity(EntityHitResult pResult) {
    }

    protected void doPostHurtEffects(LivingEntity pLiving) {
        super.doPostHurtEffects(pLiving);
    }

    protected void onHitBlock(BlockHitResult p_36755_) {
        super.onHitBlock(p_36755_);
    }

    protected boolean tryPickup(Player p_150196_) {
        return false;
    }

    protected ItemStack getPickupItem() {
        return null;
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    public void playSound(SoundEvent pSound, float pVolume, float pPitch) {
        if (pSound != this.getDefaultHitGroundSoundEvent()) {
            super.playSound(pSound, pVolume, pPitch);
        }
    }

    public void playerTouch(Player pEntity) {
    }

    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        Entity entity = this.level.getEntity(pPacket.getData());
        if (entity != null) {
            this.setOwner(entity);
        }

    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.grounded = pCompound.getBoolean("Grounded");
        if (pCompound.contains("type")) {
            this.setMeteorArrowType(pCompound.getInt("type"));
        }

        if(pCompound.contains("burnDuration")) {
            this.burnDuration = pCompound.getInt("burnDuration");
        }

        if (pCompound.contains("amplifier")) {
            this.amplifier = pCompound.getInt("amplifier");
        }

        if(pCompound.contains("damagePerMeteor")) {
            this.damagePerMeteor = pCompound.getFloat("damagePerMeteor");
        }

        if (pCompound.contains("percentAbilityPowerDealtMin")) {
            this.percentAbilityPowerDealtMin = pCompound.getFloat("percentAbilityPowerDealtMin");
        }

        if (pCompound.contains("percentAbilityPowerDealtMax")) {
            this.percentAbilityPowerDealtMax = pCompound.getFloat("percentAbilityPowerDealtMax");
        }

    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("Grounded", this.grounded);
        pCompound.putInt("type", this.getMeteorArrowType().ordinal());
        pCompound.putInt("burnDuration", this.burnDuration);
        pCompound.putInt("amplifier", this.amplifier);
        pCompound.putDouble("damagePerMeteor", this.damagePerMeteor);
        pCompound.putFloat("percentAbilityPowerDealtMin", this.percentAbilityPowerDealtMin);
        pCompound.putFloat("percentAbilityPowerDealtMax", this.percentAbilityPowerDealtMax);
    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 20) {
            this.discard();
        }

    }

    protected float getWaterInertia() {
        return 0.99F;
    }

    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }



    public static enum MeteorType {
        BASE;

        public String toString() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public static VaultMeteorArrow.MeteorType byId(int id) {
            return (VaultMeteorArrow.MeteorType) MiscUtils.getEnumEntry(VaultMeteorArrow.MeteorType.class, id);
        }
    }
}
