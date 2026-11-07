package xyz.iwolfking.woldsvaults.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import iskallia.vault.client.data.ClientPrestigePowersData;
import iskallia.vault.item.tool.JewelApplication;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.skill.base.Skill;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.iwolfking.woldsvaults.prestige.JewelSizePrestigePower;

import java.util.List;

@Mixin(targets = "iskallia.vault.client.gui.screen.block.toolstation.ToolStationApplyTab", remap = false)
public abstract class MixinToolStationApplyTab {
    @Shadow private ItemStack plannedTool;
    @Shadow private List<ItemStack> plannedTray;

    @Unique
    private boolean woldsvaults$canTryTooler() {
        if (!(plannedTool.getItem() instanceof ToolItem)) return false;
        float chance = 0;
        for (JewelSizePrestigePower power : ClientPrestigePowersData.getTree().getAll(JewelSizePrestigePower.class, Skill::isUnlocked)) {
            chance += power.getChance();
        }
        if (chance <= 0) return false;
        return plannedTray.stream().anyMatch(jewel -> !jewel.isEmpty() && jewel.getItem() instanceof JewelItem
                && ToolItem.applyJewel(plannedTool.copy(), jewel.copy(), false));
    }

    @ModifyExpressionValue(method = "drawHammer", at = @At(value = "INVOKE",
            target = "Liskallia/vault/item/tool/JewelApplication$Plan;appliedCount()I"))
    private int woldsvaults$allowToolerAttempt(int applied) {
        return applied == 0 && woldsvaults$canTryTooler() ? 1 : applied;
    }

    @Inject(method = "hammerTooltip", at = @At("HEAD"), cancellable = true)
    private void woldsvaults$toolerTooltip(JewelApplication.Plan plan, CallbackInfoReturnable<List<Component>> cir) {
        if (plan.appliedCount() == 0 && woldsvaults$canTryTooler()) {
            cir.setReturnValue(List.of(new TextComponent("Try applying with Tooler"),
                    new TextComponent("Successful Tooler rolls use no capacity.")));
        }
    }
}
