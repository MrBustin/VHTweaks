package xyz.iwolfking.woldsvaults.entities.ghosts;

import iskallia.vault.init.ModEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.quark.content.mobs.entity.Wraith;
import xyz.iwolfking.woldsvaults.entities.ghosts.lib.GenericEffectWraith;


public class DarkRedGhost extends GenericEffectWraith {
    public DarkRedGhost(EntityType<? extends Wraith> type, Level worldIn) {
        super(type, worldIn, ModEffects.BLEED, 180, 1);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        boolean did = super.doHurtTarget(entityIn);
        if (did) {
            if (entityIn instanceof LivingEntity living) {
                living.hurt(DamageSource.MAGIC, living.getMaxHealth() * 0.05F);
            }
        }

        return did;
    }
}
