package xyz.iwolfking.woldsvaults.mixin.accessors;

import iskallia.vault.item.crystal.layout.ClassicInfiniteCrystalLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClassicInfiniteCrystalLayout.class, remap = false)
public interface ClassicInfiniteCrystalLayoutAccessor {
    @Accessor("tunnelSpan")
    int getTunnelSpan();
}
