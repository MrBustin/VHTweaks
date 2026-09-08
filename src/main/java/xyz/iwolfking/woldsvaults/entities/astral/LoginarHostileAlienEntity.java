package xyz.iwolfking.woldsvaults.entities.astral;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import xyz.iwolfking.woldsvaults.init.ModSounds;

import javax.annotation.Nullable;

/*
Full credit to SilentChaos512 for the Loginar mob this is based on from Loginar Storage (https://www.curseforge.com/minecraft/mc-mods/loginar-storage)
https://github.com/SilentChaos512/Loginar-Storage
Used under the MIT license.
*/
public class LoginarHostileAlienEntity extends Monster{
    public LoginarHostileAlienEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0D).add(Attributes.MOVEMENT_SPEED, (double)0.23F).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.ARMOR, 2.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    @Override
    public boolean canStandOnFluid(FluidState fluid) {
        return fluid.is(FluidTags.LAVA);
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.LOGINAR_IDLE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.LOGINAR_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.LOGINAR_HURT;
    }
}
