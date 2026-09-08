package xyz.iwolfking.woldsvaults.mixin.raid_plaques;

import xyz.iwolfking.woldsvaults.objectives.infinite_raid.InfiniteRaidObjective;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.item.crystal.objective.RaidCrystalObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RaidCrystalObjective.class)
public class MixinRaidCrystalObjective {
    @Inject(method = "configure", at = @At("HEAD"), remap = false)
    public void configure(Vault vault, RandomSource random, String sigil, CallbackInfo ci) {
        vault.ifPresent(Vault.OBJECTIVES, objectives -> objectives.add(new InfiniteRaidObjective()));
    }
}
