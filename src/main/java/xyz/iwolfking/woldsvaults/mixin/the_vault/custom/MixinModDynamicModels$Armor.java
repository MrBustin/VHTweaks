package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.dynamodel.registry.ArmorPieceModelRegistry;
import iskallia.vault.init.ModDynamicModels;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.init.client.ModArmorModels;

@Mixin(value = {ModDynamicModels.Armor.class}, remap = false)
public class MixinModDynamicModels$Armor {
    @Shadow
    @Final
    public static ArmorPieceModelRegistry PIECE_REGISTRY;

    public MixinModDynamicModels$Armor() {
    }

    @Inject(method = {"<clinit>"}, at = {@At("TAIL")})
    private static void injectArmorModels(CallbackInfo ci) {
        PIECE_REGISTRY.registerAll(ModArmorModels.Armor.PLAGUE);
    }
}
