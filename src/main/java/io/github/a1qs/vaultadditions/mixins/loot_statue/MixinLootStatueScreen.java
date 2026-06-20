package io.github.a1qs.vaultadditions.mixins.loot_statue;

import iskallia.vault.client.gui.screen.LootStatueScreen;
import net.minecraftforge.network.simple.SimpleChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LootStatueScreen.class)
public class MixinLootStatueScreen {
    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/network/simple/SimpleChannel;sendToServer(Ljava/lang/Object;)V", remap = false))
    public <MSG> void preventSelectingOnClose(SimpleChannel instance, MSG message) {
        // Just don't send this bruh
    }
}
