package xyz.iwolfking.woldsvaults.entities.ghosts.lib;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.quark.content.mobs.entity.Wraith;

public class MagicWraith extends Wraith {

    private final float magicBonusPercentage;

    public MagicWraith(EntityType<? extends Wraith> type, Level worldIn, float magicBonusPercentage) {
        super(type, worldIn);
        this.magicBonusPercentage = magicBonusPercentage;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        super.doHurtTarget(entityIn);
        if (entityIn instanceof LivingEntity living) {
            living.hurt(DamageSource.MAGIC, f * magicBonusPercentage);
        }


        return true;
    }
}
