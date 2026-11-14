package xyz.iwolfking.woldsvaults.mixin.the_vault;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.screen.player.legacy.tab.split.pan.TalentPanRegion;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TalentPanRegion.class, remap = false)
public abstract class MixinTalentPanRegion {
    @Inject(method = "renderRowLabel", at = @At("HEAD"), cancellable = true)
    private void woldsvaults$hideTalentRowLabels(PoseStack poseStack, Font font, @Coerce Object rowStyle,
                                                 int spentPoints, boolean unlocked, CallbackInfo ci) {
        ci.cancel();
    }
}
