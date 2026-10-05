package xyz.iwolfking.woldsvaults.entities.astral;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.Level;

public class StarDevourerEntity extends Ravager {
    public StarDevourerEntity(EntityType<? extends Ravager> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.ATTACK_DAMAGE, 18.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.horizontalCollision && !this.level.isClientSide) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.1, 0));
        }
    }
}