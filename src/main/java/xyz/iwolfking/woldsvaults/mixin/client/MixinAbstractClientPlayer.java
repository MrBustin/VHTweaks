package xyz.iwolfking.woldsvaults.mixin.client;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.client.data.ClientPlayerCapeRegistry;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {

    @Inject(method = "getCloakTextureLocation", at = @At("HEAD"), cancellable = true)
    private void vault$overrideCapeTexture(CallbackInfoReturnable<ResourceLocation> cir) {
        AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;
        ResourceLocation vaultCape = ClientPlayerCapeRegistry.getCapeTexture(self.getUUID());
        if (vaultCape != null) {
            cir.setReturnValue(vaultCape);
        }
    }

    @Inject(method = "isCapeLoaded", at = @At("HEAD"), cancellable = true)
    private void vault$overrideCapeLoaded(CallbackInfoReturnable<Boolean> cir) {
        AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;
        ResourceLocation vaultCape = ClientPlayerCapeRegistry.getCapeTexture(self.getUUID());
        if (vaultCape != null) {
            cir.setReturnValue(true);
        }
    }
}
