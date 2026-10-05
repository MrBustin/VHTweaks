package xyz.iwolfking.woldsvaults.entities.astral;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SingularityCreeperEntity extends Creeper {
    public SingularityCreeperEntity(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide && this.tickCount % 2 == 0) {
            for (int i = 0; i < 3; i++) {
                double dx = this.getX() + (random.nextDouble() - 0.5) * 4.0;
                double dy = this.getY() + random.nextDouble() * 2.0;
                double dz = this.getZ() + (random.nextDouble() - 0.5) * 4.0;

                double vx = (this.getX() - dx) * 0.1;
                double vy = (this.getY() + 1.0 - dy) * 0.1;
                double vz = (this.getZ() - dz) * 0.1;
                this.level.addParticle(ParticleTypes.REVERSE_PORTAL, dx, dy, dz, vx, vy, vz);
            }
        }
    }

    public void doSingularityImplosion() {
        if (!this.level.isClientSide) {
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 0.0F, Explosion.BlockInteraction.NONE);

            List<Player> players = this.level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10.0D));
            for (Player player : players) {
                Vec3 pullVec = this.position().subtract(player.position()).normalize().scale(2.5D);
                player.setDeltaMovement(pullVec.x, 1.2D, pullVec.z);
                player.hurtMarked = true;

                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 1));
            }

            this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.5F, 0.5F);
            this.discard();
        }
    }
}