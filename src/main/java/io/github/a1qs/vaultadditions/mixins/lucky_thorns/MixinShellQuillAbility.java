package io.github.a1qs.vaultadditions.mixins.lucky_thorns;

import com.llamalad7.mixinextras.sugar.Local;
import iskallia.vault.event.GearAttributeEvents;
import iskallia.vault.skill.ability.effect.ShellQuillAbility;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(value = ShellQuillAbility.class, remap = false)
public class MixinShellQuillAbility {
    @Redirect(method = "thornsReflectDamage", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private static void triggerNormalThornsFirst(Optional<ShellQuillAbility> instance, Consumer<ShellQuillAbility> action, @Local(argsOnly = true) LivingAttackEvent event) {
        GearAttributeEvents.thornsReflectDamage(event);
        instance.ifPresent(action);
    }
}
