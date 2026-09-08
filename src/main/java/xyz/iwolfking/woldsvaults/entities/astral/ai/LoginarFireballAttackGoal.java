package xyz.iwolfking.woldsvaults.entities.astral.ai;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import xyz.iwolfking.woldsvaults.entities.astral.LoginarHostileAlienEntity;
import xyz.iwolfking.woldsvaults.entities.msc.SmallFireballSettable;
import xyz.iwolfking.woldsvaults.init.ModSounds;

import java.util.EnumSet;

public class LoginarFireballAttackGoal extends Goal {
    private final LoginarHostileAlienEntity loginar;
    private int attackStep;
    private int attackTime;
    private int lastSeen;

    public LoginarFireballAttackGoal(LoginarHostileAlienEntity entity) {
        this.loginar = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.loginar.getTarget();
        return livingentity != null && livingentity.isAlive() && this.loginar.canAttack(livingentity);
    }

    @Override
    public void start() {
        this.attackStep = 0;
    }

    @Override
    public void stop() {
        this.lastSeen = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        --this.attackTime;
        LivingEntity targetEntity = this.loginar.getTarget();
        if (targetEntity != null) {
            boolean hasLineOfSight = this.loginar.getSensing().hasLineOfSight(targetEntity);
            if (hasLineOfSight) {
                this.lastSeen = 0;
            } else {
                ++this.lastSeen;
            }

            double distanceSqr = this.loginar.distanceToSqr(targetEntity);
            if (distanceSqr < 4.0D) {
                if (!hasLineOfSight) {
                    return;
                }

                if (this.attackTime <= 0) {
                    this.attackTime = 20;
                    this.loginar.doHurtTarget(targetEntity);
                }

                this.loginar.getMoveControl().setWantedPosition(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), 1.0D);
            } else if (distanceSqr < this.getFollowDistance() * this.getFollowDistance() && hasLineOfSight) {
                double d1 = targetEntity.getX() - this.loginar.getX();
                double d2 = targetEntity.getY(0.5D) - this.loginar.getY(0.5D);
                double d3 = targetEntity.getZ() - this.loginar.getZ();
                if (this.attackTime <= 0) {
                    ++this.attackStep;
                    if (this.attackStep == 1) {
                        this.attackTime = 60;
                    } else if (this.attackStep <= 3) {
                        this.attackTime = 6;
                    } else {
                        this.attackTime = 100;
                        this.attackStep = 0;
                    }

                    if (this.attackStep > 1) {
                        double d4 = Math.sqrt(Math.sqrt(distanceSqr)) * 0.5D;
                        if (!this.loginar.isSilent()) {
                            this.loginar.level.levelEvent(null, 1018, this.loginar.blockPosition(), 0);
                        }

                        for(int i = 0; i < 1; ++i) {
                            SmallFireballSettable smallfireball = new SmallFireballSettable(this.loginar.level, this.loginar, d1 + this.loginar.getRandom().nextGaussian() * d4, d2, d3 + this.loginar.getRandom().nextGaussian() * d4, (float)this.loginar.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                            smallfireball.setPos(smallfireball.getX(), this.loginar.getY(0.5D) + 0.5D, smallfireball.getZ());
                            this.loginar.level.addFreshEntity(smallfireball);
                        }
                    }
                } else if (this.attackTime == 20 && this.attackStep == 1) {
                    loginar.level.playSound(null, loginar, ModSounds.LOGINAR_ATTACK, SoundSource.HOSTILE, 1f, 1f);
                }

                this.loginar.getLookControl().setLookAt(targetEntity, 10.0F, 10.0F);
            } else if (this.lastSeen < 5) {
                this.loginar.getMoveControl().setWantedPosition(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), 1.0D);
            }

            super.tick();
        }
    }

    private double getFollowDistance() {
        return this.loginar.getAttributeValue(Attributes.FOLLOW_RANGE);
    }
}