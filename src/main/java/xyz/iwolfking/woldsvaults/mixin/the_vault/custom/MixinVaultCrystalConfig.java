package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import iskallia.vault.config.VaultCrystalConfig;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.objective.CrystalObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.iwolfking.woldsvaults.objectives.ScalingBallisticBingoCrystalObjective;

@Mixin(value = VaultCrystalConfig.class, remap = false)
public class MixinVaultCrystalConfig {
    @WrapOperation(method = "lambda$applySeal$5", at = @At(value = "INVOKE", target = "Liskallia/vault/item/crystal/CrystalData;setObjective(Liskallia/vault/item/crystal/objective/CrystalObjective;)V"))
    private static void modifyScalingBallisticBingoObjective(CrystalData instance, CrystalObjective objective, Operation<Void> original) {
        if (objective instanceof ScalingBallisticBingoCrystalObjective && instance.getObjective() instanceof ScalingBallisticBingoCrystalObjective current) {
            instance.setObjective(new ScalingBallisticBingoCrystalObjective(current.getObjectiveProbability(), current.getSealCount() + 1));
            return;
        }

        original.call(instance, objective);
    }
}
