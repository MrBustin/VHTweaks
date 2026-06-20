package xyz.iwolfking.woldsvaults.effect.mobeffects;

import iskallia.vault.entity.scale.EntityScaleHelper;
import iskallia.vault.entity.scale.EntityScaleProfile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import xyz.iwolfking.woldsvaults.api.scale.WoldsScaleSources;

public class GrowingPotionEffect extends MobEffect {
    public GrowingPotionEffect(MobEffectCategory mobEffectCategory, int i, ResourceLocation id) {
        super(mobEffectCategory, i);
        this.setRegistryName(id);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        double newScale = (amplifier + 1) * 2;
        newScale = Math.min(newScale, 10D);
        EntityScaleHelper.setScale(
                entity,
                WoldsScaleSources.SIZE,
                (float) newScale,
                EntityScaleHelper.PEHKUI_COMPAT_TRANSITION_TICKS,
                EntityScaleProfile.BODY_MOTION_AND_VISIBILITY
        );
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        EntityScaleHelper.clearScale(entity, WoldsScaleSources.SIZE, EntityScaleHelper.PEHKUI_COMPAT_TRANSITION_TICKS);
    }
}
