package xyz.iwolfking.woldsvaults.mixin.the_vault.accessors;

import iskallia.vault.item.bottle.ManaPercentBottleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ManaPercentBottleEffect.class, remap = false)
public interface ManaPercentBottleEffectAccessor {
    @Accessor("amount")
    float getAmount();
}
