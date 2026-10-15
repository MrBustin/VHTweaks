package xyz.iwolfking.woldsvaults.entities.msc;

import iskallia.vault.entity.entity.NagaEntity;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.EntityStunnedEvent;
import iskallia.vault.util.EffectInstances;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public class ApexPredatorEntity extends NagaEntity {
    private static final double NAGA_WALK_ANIMATION_BASE_SPEED = 0.2D;
    private UUID ownerId;
    @Nullable
    private LivingEntity cachedOwner;
    private Vec3 chargeDirection = Vec3.ZERO;
    private float impactDamage;
    private float pickupRadius = 0.5F;
    private int stunDuration;
    private double chargeSpeed = 0.8D;
    private int remainingTicks = 100;
    private boolean finishing;

    public ApexPredatorEntity(EntityType<? extends ApexPredatorEntity> type, Level level) {
        super(type, level);
        this.setNoAi(true);
        this.setNoGravity(false);
        this.maxUpStep = 1.0F;
        this.setInvulnerable(true);
    }

    public void configure(ServerPlayer owner, Vec3 direction, double speed, float damage, float radius,
                          int stunDuration, int duration) {
        this.ownerId = owner.getUUID();
        this.cachedOwner = owner;
        this.chargeDirection = direction.multiply(1, 0, 1).normalize();
        this.chargeSpeed = speed;
        this.impactDamage = damage;
        this.pickupRadius = Math.max(0.0F, radius);
        this.stunDuration = Math.max(0, stunDuration);
        this.remainingTicks = duration;

        float yaw = (float) (Mth.atan2(-chargeDirection.x, chargeDirection.z) * Mth.RAD_TO_DEG);

        this.setYRot(yaw);
        this.yRotO = yaw;
        this.setYHeadRot(yaw);
        this.setDeltaMovement(chargeDirection.scale(speed));
    }

    private <E extends IAnimatable> PlayState animationPredicate(AnimationEvent<E> event) {
        AnimationController<E> controller = event.getController();
        double horizontalSpeed = getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).length();

        if (event.isMoving() || horizontalSpeed > 1.0E-4D) {
            double animationSpeed = Mth.clamp(
                    horizontalSpeed / NAGA_WALK_ANIMATION_BASE_SPEED,
                    0.25D,
                    4.0D
            );
            controller.setAnimationSpeed(animationSpeed);
            controller.setAnimation(new AnimationBuilder().loop("animation.champion.walking"));
        } else {
            controller.setAnimationSpeed(1.0D);
            controller.setAnimation(new AnimationBuilder().loop("animation.champion.idle"));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(
                this,
                "controller",
                0.0F,
                this::animationPredicate
        ));
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide || finishing) {
            return;
        }

        if (--remainingTicks <= 0 || hasTwoBlockHighObstacle()) {
            finishCharge();
            return;
        }

        double verticalSpeed = getDeltaMovement().y;
        if (!isNoGravity()) {
            verticalSpeed -= 0.08D;
        }

        Vec3 chargeMotion = new Vec3(
                chargeDirection.x * chargeSpeed,
                verticalSpeed,
                chargeDirection.z * chargeSpeed
        );

        // With AI disabled, Mob/LivingEntity movement consumes the velocity set on
        // the previous tick. Move explicitly so the charge remains authoritative,
        // including vanilla-style gravity and step-up collision handling.
        move(MoverType.SELF, chargeMotion);

        double nextVerticalSpeed = verticalCollision ? 0.0D : verticalSpeed * 0.98D;
        setDeltaMovement(chargeMotion.x, nextVerticalSpeed, chargeMotion.z);
        hasImpulse = true;

        setYRot(yRotO);
        collectEntities();
    }

    private boolean hasTwoBlockHighObstacle() {
        double checkDistance = chargeSpeed + 0.35D;

        Vec3 feetStart = position().add(0, 0.15D, 0);
        Vec3 headStart = position().add(0, 1.15D, 0);

        Vec3 offset = chargeDirection.scale(checkDistance);

        BlockHitResult lower = level.clip(new ClipContext(
                feetStart,
                feetStart.add(offset),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));

        BlockHitResult upper = level.clip(new ClipContext(
                headStart,
                headStart.add(offset),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));

        return lower.getType() != HitResult.Type.MISS
                && upper.getType() != HitResult.Type.MISS;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().size() < 12;
    }

    private void collectEntities() {
        AABB pickupArea = getBoundingBox().inflate(pickupRadius);

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, pickupArea, this::canCollect)) {
            target.startRiding(this, true);
        }
    }

    private boolean canCollect(LivingEntity target) {
        return target.isAlive()
                && (ownerId == null || !ownerId.equals(target.getUUID()))
                && !target.isPassenger()
                && !(target instanceof ApexPredatorEntity);
    }

    @Nullable
    public LivingEntity getOwner() {
        if (cachedOwner != null && !cachedOwner.isRemoved()) {
            return cachedOwner;
        }

        cachedOwner = null;
        if (ownerId != null && level instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(ownerId);
            if (entity instanceof LivingEntity living) {
                cachedOwner = living;
            }
        }

        return cachedOwner;
    }

    @Override
    public void positionRider(Entity passenger) {
        int index = getPassengers().indexOf(passenger);
        double side = (index % 2 == 0 ? -1 : 1) * (0.45D + index * 0.08D);
        double behind = 0.3D + (index / 2) * 0.45D;

        Vec3 sideways = new Vec3(-chargeDirection.z, 0, chargeDirection.x)
                .scale(side);
        Vec3 backwards = chargeDirection.scale(-behind);

        passenger.setPos(
                getX() + sideways.x + backwards.x,
                getY() + 0.8D,
                getZ() + sideways.z + backwards.z
        );
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (ownerId != null) {
            tag.putUUID("Owner", ownerId);
        }

        tag.putDouble("ChargeX", chargeDirection.x);
        tag.putDouble("ChargeY", chargeDirection.y);
        tag.putDouble("ChargeZ", chargeDirection.z);
        tag.putDouble("ChargeSpeed", chargeSpeed);
        tag.putFloat("ImpactDamage", impactDamage);
        tag.putFloat("PickupRadius", pickupRadius);
        tag.putInt("StunDuration", stunDuration);
        tag.putInt("RemainingTicks", remainingTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.hasUUID("Owner")) {
            ownerId = tag.getUUID("Owner");
        }

        chargeDirection = new Vec3(
                tag.getDouble("ChargeX"),
                tag.getDouble("ChargeY"),
                tag.getDouble("ChargeZ")
        );
        chargeSpeed = tag.getDouble("ChargeSpeed");
        impactDamage = tag.getFloat("ImpactDamage");
        pickupRadius = tag.contains("PickupRadius") ? tag.getFloat("PickupRadius") : 0.5F;
        stunDuration = tag.getInt("StunDuration");
        remainingTicks = tag.getInt("RemainingTicks");
    }

    private void finishCharge() {
        if (finishing) {
            return;
        }

        finishing = true;
        List<Entity> carried = List.copyOf(getPassengers());
        ejectPassengers();

        LivingEntity owner = getOwner();

        for (Entity entity : carried) {
            if (!(entity instanceof LivingEntity living) || !living.isAlive()) {
                continue;
            }

            DamageSource source = owner != null ? DamageSource.mobAttack(owner) : DamageSource.GENERIC;

            living.hurt(source, impactDamage);

            if (stunDuration > 0) {
                living.addEffect(EffectInstances.stunning(stunDuration));
                if (owner != null) {
                    CommonEvents.ENTITY_STUNNED.invoke(new EntityStunnedEvent.Data(owner, living));
                }
            }

            Vec3 knockback = chargeDirection.scale(1.5D).add(0, 0.45D, 0);
            living.setDeltaMovement(knockback);
            living.hurtMarked = true;
        }

        discard();
    }
}
