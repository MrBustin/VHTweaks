package xyz.iwolfking.woldsvaults.mixin.the_vault.temple;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.challenge.action.FloatingItemRewardChallengeAction;
import iskallia.vault.core.vault.challenge.action.TileRewardChallengeAction;
import iskallia.vault.core.vault.challenge.base.ChallengeManager;
import iskallia.vault.core.vault.challenge.temple.TempleChallengeManager;
import iskallia.vault.core.world.storage.BlockCuboid;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {TileRewardChallengeAction.class, FloatingItemRewardChallengeAction.class}, remap = false)
public abstract class MixinTempleRewardActions {
    @Inject(
            method = "onActivate",
            at = @At(value = "INVOKE", target = "Liskallia/vault/core/random/RandomSource;nextInt(I)I", ordinal = 0),
            cancellable = true
    )
    private void woldsvaults$limitTempleRewardHeight(ServerLevel world, ChallengeManager manager, RandomSource random,
                                                   CallbackInfo ci, @Local(ordinal = 0) LocalRef<BlockCuboid> rewardZone) {
        if (!(manager instanceof TempleChallengeManager)) return;

        // This is the final world-space placement zone, after Temple's -17 Y and horizontal insets.
        // Clipping manager.getZone() earlier would lower the intended ceiling by another 17 blocks.
        BlockCuboid capped = woldsvaults$capRewardZone(rewardZone.get(), manager.getPos().getY());
        if (capped == null) {
            // No eligible space: finish this reward action normally so the challenge can complete.
            ci.cancel();
            return;
        }
        rewardZone.set(capped);
    }

    @Unique
    private static BlockCuboid woldsvaults$capRewardZone(BlockCuboid zone, int ceilingY) {
        int maxY = Math.min(zone.getMaxY(), ceilingY);
        if (zone.getMinY() > maxY || zone.getMinX() > zone.getMaxX() || zone.getMinZ() > zone.getMaxZ()) {
            return null;
        }
        if (zone.getMaxY() == maxY) return zone;

        return BlockCuboid.of(zone.getMinX(), zone.getMinY(), zone.getMinZ(),
                zone.getMaxX(), maxY, zone.getMaxZ());
    }
}
