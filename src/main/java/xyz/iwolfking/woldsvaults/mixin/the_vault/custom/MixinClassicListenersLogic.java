package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.core.vault.player.ClassicListenersLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClassicListenersLogic.class, remap = false)
public class MixinClassicListenersLogic {
    @Inject(method = "getVaultObjective", at = @At("HEAD"), cancellable = true)
    private void getCustomVaultObjectiveName(String key, CallbackInfoReturnable<String> cir) {
        if ("scaling_ballistic_bingo".equals(key)) {
            cir.setReturnValue("Ballistic Bingo");
        }
    }
}
