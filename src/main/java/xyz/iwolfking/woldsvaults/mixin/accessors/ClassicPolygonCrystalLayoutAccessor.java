package xyz.iwolfking.woldsvaults.mixin.accessors;

import iskallia.vault.item.crystal.layout.ClassicPolygonCrystalLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClassicPolygonCrystalLayout.class, remap = false)
public interface ClassicPolygonCrystalLayoutAccessor {
    @Accessor("vertices")
    int[] getVertices();

}
