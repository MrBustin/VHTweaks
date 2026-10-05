package xyz.iwolfking.woldsvaults.mixin.patreon;

import iskallia.vault.www.Reward;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.iwolfking.woldsvaults.configs.Configs;

@Mixin(targets = "iskallia.vault.client.gui.framework.element.DiscoveredModelSelectElement$DiscoveredModelSelectorModel", remap = false)
public class MixinDiscoveredModelSelectorModel {
    /**
     * Keep the normal VH reward filter, but do not hide a reward-gated model that
     * has been explicitly granted to this UUID by the transmog unlock config.
     */
    @Redirect(
            method = "lambda$getEntries$4",
            at = @At(
                    value = "INVOKE",
                    target = "Liskallia/vault/www/Reward;hasModel(Liskallia/vault/www/Reward$ArmorPiece;Lnet/minecraft/resources/ResourceLocation;)Z"
            )
    )
    private static boolean addConfiguredUuidVisibility(Reward reward, Reward.ArmorPiece armorPiece,
                                                        ResourceLocation modelId) {
        if (reward.hasModel(armorPiece, modelId)) {
            return true;
        }

        Player player = Minecraft.getInstance().player;
        return player != null && Configs.TRANSMOG_UNLOCKS.hasAccess(player, modelId);
    }
}
