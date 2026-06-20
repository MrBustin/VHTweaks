package io.github.a1qs.vaultadditions.mixins.additional_gear_cores;

import io.github.a1qs.vaultadditions.item.recipe.DamageGearAnvilRecipe;
import io.github.a1qs.vaultadditions.item.recipe.FractureGearAnvilRecipe;
import iskallia.vault.recipe.anvil.AnvilContext;
import iskallia.vault.recipe.anvil.AnvilRecipe;
import iskallia.vault.recipe.anvil.AnvilRecipes;
import iskallia.vault.recipe.anvil.VanillaAnvilRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AnvilRecipes.class, remap = false)
public abstract class MixinAnvilRecipes {
    @Inject(method = "register()V", at = @At("TAIL"))
    private static void register(CallbackInfo ci) {
        register(new DamageGearAnvilRecipe());
        register(new FractureGearAnvilRecipe());
    }

    @Shadow
    private static <T extends AnvilRecipe> T register(T recipe) {
        throw new AssertionError("Mixin failed to inject");
    }
}
