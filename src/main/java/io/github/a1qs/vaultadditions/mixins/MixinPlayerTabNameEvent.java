package io.github.a1qs.vaultadditions.mixins;

import iskallia.vault.event.PlayerTabNameEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerTabNameEvent.class, remap = false)
public class MixinPlayerTabNameEvent {

    /**
     * Cancelling the VaultHunters implementation to replace it with our own in <br>
     * {@link TabListNameFormatEvent#onTabListNameFormat(PlayerEvent.TabListNameFormat)}
     */
    @Inject(method = "onTabListNameFormat", at = @At("HEAD"), cancellable = true)
    private static void cancelTabFormat(PlayerEvent.TabListNameFormat event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true)
    private static void cancelTabFormat(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
