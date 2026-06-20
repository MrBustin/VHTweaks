package io.github.a1qs.vaultadditions.mixins.registry;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.config.Configs;
import iskallia.vault.init.ModConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModConfigs.class, remap = false)
public class MixinModConfigs {

    @Inject(method = "register", at = @At("TAIL"))
    private static void injectRegistries(CallbackInfo ci) {
        Configs.registerCustomConfigs();
        VaultAdditions.LOGGER.info("Successfully loaded custom Vault Configs");
    }
}
