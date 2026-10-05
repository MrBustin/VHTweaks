package xyz.iwolfking.woldsvaults.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.client.data.ClientPlayerCapeRegistry;

@Mixin(PlayerInfo.class)
public abstract class MixinPlayerInfo {

    @Inject(method = "isCapeLoaded", at = @At("HEAD"), cancellable = true)
    private void vault$overrideCapeLoaded(CallbackInfoReturnable<Boolean> cir) {
        PlayerInfo self = (PlayerInfo) (Object) this;
        GameProfile profile = self.getProfile();
        if (profile == null) return;

        ResourceLocation vaultCape = ClientPlayerCapeRegistry.getCapeTexture(profile.getId());
        if (vaultCape != null) {
            cir.setReturnValue(true);
        }
    }
}
