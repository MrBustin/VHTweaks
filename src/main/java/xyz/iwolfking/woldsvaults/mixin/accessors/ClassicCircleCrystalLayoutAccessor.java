package xyz.iwolfking.woldsvaults.mixin.accessors;

import iskallia.vault.item.crystal.layout.ClassicCircleCrystalLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClassicCircleCrystalLayout.class, remap = false)
public interface ClassicCircleCrystalLayoutAccessor {
    @Accessor("radius")
    int getRadius();

}
