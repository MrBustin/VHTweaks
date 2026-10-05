package xyz.iwolfking.woldsvaults.mixin.patreon;

import iskallia.vault.block.TransmogTableBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = TransmogTableBlock.class, remap = false)
public class MixinTransmogTableBlock {
//    @Inject(method = "canTransmogModel", at = @At("HEAD"), cancellable = true)
//    private static void preventPatreonCheck(Player player, Collection<ResourceLocation> discoveredModelIds, ResourceLocation modelId, CallbackInfoReturnable<Boolean> cir) {
//        cir.setReturnValue(discoveredModelIds.contains(modelId));
//    }
}
