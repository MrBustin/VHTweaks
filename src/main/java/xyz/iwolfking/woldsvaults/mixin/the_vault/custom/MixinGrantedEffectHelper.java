package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.util.calc.GrantedEffectHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import xyz.iwolfking.woldsvaults.effect.mobeffects.SaferSpacePotionEffect;

import java.util.Map;

@Mixin(value = GrantedEffectHelper.class, remap = false)
public class MixinGrantedEffectHelper {

    /**
     * @author aida
     * @reason keep safer space visible
     */
    @Overwrite
    public static void applyEffects(LivingEntity entity, Map<MobEffect, Integer> effects) {
        effects.forEach(
                (effect, amplifier) -> {
                    if (amplifier >= 0) {
                        MobEffectInstance activeEffect = entity.getEffect(effect);
                        if (activeEffect == null
                                || activeEffect.getAmplifier() < amplifier
                                || activeEffect.getAmplifier() == amplifier && activeEffect.getDuration() <= 259) {
                            boolean beVisible = effect instanceof SaferSpacePotionEffect;
                            entity.addEffect(new MobEffectInstance(effect, 339, amplifier, true, beVisible, beVisible));
                        }
                    }
                }
        );
    }
}
