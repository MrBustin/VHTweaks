package xyz.iwolfking.woldsvaults.mixin.the_vault.custom;

import iskallia.vault.container.ToolStationContainer;
import iskallia.vault.item.tool.JewelApplication;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.iwolfking.woldsvaults.util.ToolerPrestigeApplicationContext;

import java.util.List;

@Mixin(value = ToolStationContainer.class, remap = false)
public abstract class MixinToolStationContainer {
    @Redirect(method = "applyTray", at = @At(value = "INVOKE", target = "Liskallia/vault/item/tool/JewelApplication;plan(Lnet/minecraft/world/item/ItemStack;Ljava/util/List;)Liskallia/vault/item/tool/JewelApplication$Plan;"))
    private JewelApplication.Plan woldsvaults$applyWithPrestige(ItemStack tool, List<ItemStack> tray, ServerPlayer player) {
        Player previous = ToolerPrestigeApplicationContext.getPlayer();
        ToolerPrestigeApplicationContext.setPlayer(player);
        try {
            return JewelApplication.plan(tool, tray);
        } finally {
            if (previous == null) {
                ToolerPrestigeApplicationContext.clearPlayer();
            } else {
                ToolerPrestigeApplicationContext.setPlayer(previous);
            }
        }
    }
}
