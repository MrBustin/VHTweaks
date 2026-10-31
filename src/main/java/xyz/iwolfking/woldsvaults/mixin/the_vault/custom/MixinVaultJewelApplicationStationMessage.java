package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.block.entity.VaultJewelApplicationStationTileEntity;
import iskallia.vault.network.message.VaultJewelApplicationStationMessage;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.iwolfking.woldsvaults.util.ToolerPrestigeApplicationContext;

@Mixin(value = VaultJewelApplicationStationMessage.class, remap = false)
public abstract class MixinVaultJewelApplicationStationMessage {


    @Redirect(method = "lambda$handle$0", at = @At(value = "INVOKE", target = "Liskallia/vault/block/entity/VaultJewelApplicationStationTileEntity;applyJewels()V", remap = false), remap = false)
    private static void applyJewelsWithPlayer(VaultJewelApplicationStationTileEntity station, NetworkEvent.Context context) {
        ToolerPrestigeApplicationContext.setPlayer(context.getSender());
        try {
            station.applyJewels();
        } finally {
            ToolerPrestigeApplicationContext.clearPlayer();
        }
    }
}
