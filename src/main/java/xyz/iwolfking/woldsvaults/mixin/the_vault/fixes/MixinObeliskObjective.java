package xyz.iwolfking.woldsvaults.mixin.the_vault.fixes;

import com.mojang.blaze3d.platform.Window;
import iskallia.vault.core.vault.objective.ObeliskObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ObeliskObjective.class)
public class MixinObeliskObjective {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getGuiScaledWidth()I"))
    private int fixObjectiveRendering(Window instance) {
        return 0;
    }
}
