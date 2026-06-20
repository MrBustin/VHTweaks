package xyz.iwolfking.woldsvaults.effect.mobeffects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleType;

public class GrowingPotionEffect extends MobEffect {
    private final ScaleType scaleType;

    public GrowingPotionEffect(MobEffectCategory mobEffectCategory, int i, ScaleType scaleType, ResourceLocation id) {
        super(mobEffectCategory, i);
        this.scaleType = scaleType;
        this.setRegistryName(id);
    }


    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        ScaleData scaleData = scaleType.getScaleData(entity);
        double newScale = (amplifier + 1) * 2;
        newScale = Math.min(newScale, 10D);
        scaleData.setTargetScale((float) newScale);
        scaleData.setScaleTickDelay(scaleData.getScaleTickDelay());
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        ScaleData scaleData = scaleType.getScaleData(entity);
        scaleData.setTargetScale(1.0F);
        scaleData.setScaleTickDelay(scaleData.getScaleTickDelay());
    }
}
