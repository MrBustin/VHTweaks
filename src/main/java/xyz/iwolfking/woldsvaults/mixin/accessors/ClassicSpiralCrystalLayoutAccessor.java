package xyz.iwolfking.woldsvaults.mixin.accessors;

import iskallia.vault.item.crystal.layout.ClassicSpiralCrystalLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import xyz.iwolfking.vhapi.api.loaders.vault.crystal.VaultCrystalConfigLoader;

@Mixin(value = ClassicSpiralCrystalLayout.class, remap = false)
public interface ClassicSpiralCrystalLayoutAccessor {
    @Accessor("halfLength")
    int getHalfLength();

}
