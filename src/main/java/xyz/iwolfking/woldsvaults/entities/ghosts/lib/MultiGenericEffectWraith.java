package xyz.iwolfking.woldsvaults.entities.ghosts.lib;

import iskallia.vault.util.data.WeightedList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.quark.content.mobs.entity.Wraith;

public class MultiGenericEffectWraith extends Wraith {

    private final WeightedList<MobEffectInstance> effectList;
    public MultiGenericEffectWraith(EntityType<? extends Wraith> type, Level worldIn, WeightedList<MobEffectInstance> effectList) {
        super(type, worldIn);
        this.effectList = effectList;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        boolean did = super.doHurtTarget(entityIn);
        if (did) {
            if (entityIn instanceof LivingEntity living) {
                if(effectList != null) {
                    if(!effectList.isEmpty()) {
                        MobEffectInstance effect = effectList.getRandom(random);
                        if(effect != null) {
                            living.addEffect(new MobEffectInstance(effect));
                        }

                    }
                }

            }
        }

        return did;
    }
}
