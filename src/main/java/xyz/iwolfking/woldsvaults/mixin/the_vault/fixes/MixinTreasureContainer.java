package xyz.iwolfking.woldsvaults.mixin.the_vault.fixes;

import iskallia.vault.container.TreasureContainerContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = TreasureContainerContainer.class, remap = false)
public class MixinTreasureContainer {

    // Match the development and production names of this Minecraft override.
    @ModifyConstant(
            method = {"broadcastChanges()V", "m_38946_()V"},
            constant = @Constant(intValue = 18),
            remap = false
    )
    private int speedUpReveal(int original) {
        return 4; // Reveal one occupied slot every 4 ticks
    }
}
