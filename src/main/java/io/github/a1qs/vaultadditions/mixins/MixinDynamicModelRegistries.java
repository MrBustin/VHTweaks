package io.github.a1qs.vaultadditions.mixins;

import iskallia.vault.dynamodel.registry.DynamicModelRegistries;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.init.ModDynamicModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = DynamicModelRegistries.class, remap = false)
public class MixinDynamicModelRegistries {
    @Inject(method = "getUniqueRegistries", at = @At("RETURN"), cancellable = true)
    public void addArmorModelRegistry(CallbackInfoReturnable<Set<DynamicModelRegistry<?>>> cir) {
        Set<DynamicModelRegistry<?>> registries = new HashSet<>(cir.getReturnValue());
        registries.add(ModDynamicModels.Armor.MODEL_REGISTRY);
        cir.setReturnValue(Collections.unmodifiableSet(registries));
    }
}
