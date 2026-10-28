package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.config.altar.VaultAltarIngredientsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = VaultAltarIngredientsConfig.class, remap = false)
public abstract class VaultAltarIngredientsConfigMixin {


    @ModifyVariable(
            method = "getScale",
            at = @At("STORE"),
            ordinal = 3
    )
    private double vhafterdark$replaceMiscScale(double original, String poolId, int crystalsCrafted) {
        final double SLOPE_DIV = 32.0D;
        final double CAP       = 227.0D;
        final double SHARPNESS = 4.0D;

        double linear = crystalsCrafted / SLOPE_DIV;
        return linear / Math.pow(1.0D + Math.pow(linear / CAP, SHARPNESS), 1.0D / SHARPNESS);
    }
}