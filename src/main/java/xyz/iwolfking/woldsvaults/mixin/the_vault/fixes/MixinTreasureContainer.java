package xyz.iwolfking.woldsvaults.mixin.the_vault.fixes;

import iskallia.vault.container.TreasureContainerContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = TreasureContainerContainer.class, remap = false)
public class MixinTreasureContainer {

    @ModifyConstant(method = "broadcastChanges", constant = @Constant(intValue = 18))
    private int speedUpReveal(int original) {
        return 4; // Reveal one occupied slot every 4 ticks
    }
}
