package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.dynamodel.registry.DynamicModelRegistries;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.models.Jewels;

@Mixin(value = ModDynamicModels.class, remap = false)
public class MixinModDynamicModels {
    @Shadow @Final public static DynamicModelRegistries REGISTRIES;

    @Inject(method = "initItemAssociations", at = @At("TAIL"))
    private static void addJewelAssociation(CallbackInfo ci) {
        REGISTRIES.associate(ModItems.JEWEL, Jewels.REGISTRY);
    }
}
