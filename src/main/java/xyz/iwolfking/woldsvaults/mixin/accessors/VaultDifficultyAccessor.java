package xyz.iwolfking.woldsvaults.mixin.accessors;

import iskallia.vault.world.VaultDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = VaultDifficulty.class, remap = false)
public interface VaultDifficultyAccessor {
    @Accessor("displayOrder")
    int getDisplayOrder();
}
