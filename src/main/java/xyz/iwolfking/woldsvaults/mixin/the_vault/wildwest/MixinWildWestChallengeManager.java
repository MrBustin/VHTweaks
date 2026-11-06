package xyz.iwolfking.woldsvaults.mixin.the_vault.wildwest;

import iskallia.vault.core.vault.challenge.wildwest.WildWestChallengeManager;
import iskallia.vault.core.world.storage.BlockCuboid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.server.level.ServerLevel;

@Mixin(value = WildWestChallengeManager.class, remap = false)
public abstract class MixinWildWestChallengeManager {
    @Shadow private BlockCuboid zone;
    @Unique private boolean woldsvaults$activatingRewards;

    @Inject(method = "onCashOut", at = @At("HEAD"))
    private void woldsvaults$beginRewardActivation(ServerLevel world, CallbackInfo ci) {
        this.woldsvaults$activatingRewards = true;
    }

    @Inject(method = "onCashOut", at = @At("RETURN"))
    private void woldsvaults$endRewardActivation(ServerLevel world, CallbackInfo ci) {
        this.woldsvaults$activatingRewards = false;
    }

    @Inject(method = "getZone", at = @At("HEAD"), cancellable = true)
    private void woldsvaults$limitRewardZoneHeight(CallbackInfoReturnable<BlockCuboid> cir) {
        if (!this.woldsvaults$activatingRewards || this.zone == null) return;

        // Challenge zones are controller-relative; getActiveZone() offsets them by the controller position.
        // A relative Y ceiling of 0 therefore matches the Wild West controller's height.
        int maxY = Math.min(this.zone.getMaxY(), 0);
        int minY = Math.min(this.zone.getMinY(), maxY);
        cir.setReturnValue(BlockCuboid.of(this.zone.getMinX(), minY, this.zone.getMinZ(),
                this.zone.getMaxX(), maxY, this.zone.getMaxZ()));
    }
}
