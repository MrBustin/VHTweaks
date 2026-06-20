package io.github.a1qs.vaultadditions.mixins.power_crystals;

import io.github.a1qs.vaultadditions.config.ServerConfigs;
import io.github.a1qs.vaultadditions.init.ModNetwork;
import io.github.a1qs.vaultadditions.network.ServerboundOpenPowersMessage;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.gui.screen.player.element.SkillTabContainerElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static io.github.a1qs.vaultadditions.vault.menu.PowerElementContainerScreen.TAB_INDEX;

@Mixin(value = SkillTabContainerElement.class, remap = false)
public class MixinSkillTabContainerElement {
    @ModifyVariable(
            method = "<init>",
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private TextureAtlasRegion[] modifyIcons(TextureAtlasRegion[] icons) {
        TextureAtlasRegion[] newIcons;
        if(ServerConfigs.SHOW_POWER_MENU.get() || VaultBarOverlay.vaultLevel >= 100) {
            newIcons = new TextureAtlasRegion[icons.length + 1];
            System.arraycopy(icons, 0, newIcons, 0, icons.length);
            newIcons[icons.length] = ScreenTextures.TAB_ICON_ARCHETYPES;
            return newIcons;
        } else {
            return icons;
        }
    }


    @Inject(method = "lambda$new$0(II)V", at = @At("TAIL"))
    private static void handleAdditionalIndex(int selectedIndex, int index, CallbackInfo ci) {
        if (selectedIndex != index && index == TAB_INDEX) {
            ModNetwork.CHANNEL.sendToServer(ServerboundOpenPowersMessage.INSTANCE);
        }
    }
}
