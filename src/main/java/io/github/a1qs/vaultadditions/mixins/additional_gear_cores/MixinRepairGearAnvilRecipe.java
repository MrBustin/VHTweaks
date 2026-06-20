package io.github.a1qs.vaultadditions.mixins.additional_gear_cores;

import iskallia.vault.recipe.anvil.AnvilContext;
import iskallia.vault.recipe.anvil.RepairGearAnvilRecipe;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = RepairGearAnvilRecipe.class, remap = false)
public class MixinRepairGearAnvilRecipe {
    @Inject(method = "onSimpleCraft", at = @At(value = "INVOKE", target = "Liskallia/vault/gear/data/VaultGearData;read(Lnet/minecraft/world/item/ItemStack;)Liskallia/vault/gear/data/VaultGearData;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void onlyRepairDamagedGear(AnvilContext context, CallbackInfoReturnable<Boolean> cir, ItemStack primary, ItemStack secondary) {
        if (primary.getDamageValue() == 0) {
            cir.setReturnValue(false);
        }
    }
}
