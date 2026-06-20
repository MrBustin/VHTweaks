package io.github.a1qs.vaultadditions.mixins.registry;

import io.github.a1qs.vaultadditions.vault.core.time.modifier.PowerCrystalExtension;
import iskallia.vault.core.data.key.registry.SupplierRegistry;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.time.modifier.ClockModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VaultRegistry.class, remap = false)
public class MixinVaultRegistry {
    @Shadow @Final public static SupplierRegistry<ClockModifier> CLOCK_MODIFIER;

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void injectRegistries(CallbackInfo ci) {
        CLOCK_MODIFIER.add(PowerCrystalExtension.KEY);
    }
}
