package xyz.iwolfking.woldsvaults.entities;

import iskallia.vault.entity.VaultBoss;
import iskallia.vault.entity.ai.*;
import iskallia.vault.entity.entity.EternalEntity;
import xyz.iwolfking.woldsvaults.init.ModSounds;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class WoldBoss extends Zombie implements VaultBoss {

    public TeleportRandomly<WoldBoss> teleportTask = new TeleportRandomly(this, new TeleportRandomly.Condition[]{(entity, source, amount) -> {
        return !(source.getEntity() instanceof LivingEntity) ? 0.2 : 0.0;
    }});
    public final ServerBossEvent bossInfo;
    public RegenAfterAWhile<WoldBoss> regenAfterAWhile;

    public WoldBoss(EntityType<? extends Zombie> p_34271_, Level p_34272_) {
        super(p_34271_, p_34272_);
        this.bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
        this.regenAfterAWhile = new RegenAfterAWhile(this);
    }

    protected void dropFromLootTable(DamageSource damageSource, boolean attackedRecently) {
    }

    protected void doUnderWaterConversion() {
    }

    protected void addBehaviourGoals() {
        super.addBehaviourGoals();
        this.goalSelector.addGoal(1, TeleportGoal.builder(this).start((entity) -> {
            return entity.getTarget() != null && entity.tickCount % 60 == 0;
        }).to((entity) -> {
            return entity.getTarget().position().add((entity.random.nextDouble() - 0.5) * 8.0, (double)(entity.random.nextInt(16) - 8), (entity.random.nextDouble() - 0.5) * 8.0);
        }).then((entity) -> {
            entity.playSound(iskallia.vault.init.ModSounds.BOSS_TP_SFX, 1.0F, 1.0F);
        }).build());
        this.goalSelector.addGoal(1, new ThrowProjectilesGoal(this, 96, 10, BALLS));
        this.goalSelector.addGoal(1, new AOEGoal(this, (e) -> {
            return !(e instanceof VaultBoss);
        }));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, false));
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(100.0);
    }

    protected boolean isSunSensitive() {
        return false;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (!(source.getEntity() instanceof Player) && !(source.getEntity() instanceof EternalEntity) && source != DamageSource.OUT_OF_WORLD) {
            return false;
        } else if (!this.isInvulnerableTo(source) && source != DamageSource.FALL) {
            if (this.teleportTask.attackEntityFrom(source, amount)) {
                return true;
            } else {
                this.regenAfterAWhile.onDamageTaken();
                return super.hurt(source, amount);
            }
        } else {
            return false;
        }
    }

    public ServerBossEvent getServerBossInfo() {
        return this.bossInfo;
    }

    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
            this.regenAfterAWhile.tick();
        }

    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.WOLD_AMBIENT;
    }

    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.WOLD_HURT;
    }

    public SoundEvent getDeathSound() {
        return ModSounds.WOLD_DEATH;
    }

    public static final ThrowProjectilesGoal.Projectile BALLS = (world1, shooter) -> {
        return new Snowball(world1, shooter) {
            protected void onHitEntity(EntityHitResult raycast) {
                Entity entity = raycast.getEntity();
                if (entity != shooter) {
                    int i = entity instanceof Blaze ? 6 : 3;
                    entity.hurt(DamageSource.indirectMobAttack(this, shooter), (float)i);
                }
            }
        };
    };
}
