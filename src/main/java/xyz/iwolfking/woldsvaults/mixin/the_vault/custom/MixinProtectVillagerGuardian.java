package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.entity.entity.guardian.AbstractGuardianEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.core.vault.challenge.protectvillager.ProtectVillagerPathGoal;

@Mixin(AbstractGuardianEntity.class)
public abstract class MixinProtectVillagerGuardian {
    @Inject(method = "customServerAiStep", at = @At("HEAD"), cancellable = true)
    private void woldsvaults$pauseBrainWhileFollowingWaypoint(CallbackInfo ci) {
        AbstractGuardianEntity guardian = (AbstractGuardianEntity) (Object) this;
        // Goal priorities do not arbitrate brain behaviors. Resume the normal brain as
        // soon as routing finishes or is interrupted, including when the villager dies.
        if (guardian.goalSelector.getRunningGoals().anyMatch(goal -> goal.getGoal() instanceof ProtectVillagerPathGoal)) {
            ci.cancel();
        }
    }
}
