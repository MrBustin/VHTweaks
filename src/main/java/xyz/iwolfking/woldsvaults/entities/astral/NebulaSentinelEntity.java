package xyz.iwolfking.woldsvaults.entities.astral;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class NebulaSentinelEntity extends IronGolem implements PowerableMob {
    public NebulaSentinelEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean isPlayerCreated() {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.tickCount % 100 == 0 && !this.level.isClientSide) {
            double range = 10.0D;
            List<Player> players = this.level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(range));

            for (Player player : players) {
                if(!this.hasLineOfSight(player)) {
                    return;
                }

                Vec3 pullVec = this.position().subtract(player.position());
                double distance = pullVec.length();

                if (distance > 2.0D) {
                    Vec3 motion = pullVec.normalize().scale(0.85D).add(0, 0.2D, 0);

                    player.setDeltaMovement(motion.x, motion.y, motion.z);

                    player.hurtMarked = true;

                    if (this.level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        for (int i = 0; i < 5; i++) {
                            double lerp = i / 5.0D;
                            double pX = player.getX() + (this.getX() - player.getX()) * lerp;
                            double pY = player.getY(0.5D) + (this.getY(0.5D) - player.getY(0.5D)) * lerp;
                            double pZ = player.getZ() + (this.getZ() - player.getZ()) * lerp;

                            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL,
                                    pX, pY, pZ, 2, 0.1D, 0.1D, 0.1D, 0.0D);
                        }
                    }

                    this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 0.5F);
                }
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return IronGolem.createAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    public boolean isPowered() {
        return true;
    }
}