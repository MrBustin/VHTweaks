package xyz.iwolfking.woldsvaults.mixin.the_vault.fixes;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultUtils;
import iskallia.vault.core.vault.objective.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.objectives.HauntedBraziersObjective;

@Mixin(value = VaultUtils.class, remap = false)
public abstract class MixinVaultUtils {
    @Shadow
    public static boolean hasObjective(Vault vault, Class<? extends Objective> objectiveClass) {
        return false;
    }

    @Inject(method = "isBrazierVault", at = @At("HEAD"), cancellable = true)
    private static void isHauntedBraziersVault(Vault vault, CallbackInfoReturnable<Boolean> cir) {
        if (hasObjective(vault, HauntedBraziersObjective.class)) {
            cir.setReturnValue(true);
        }
    }
}
