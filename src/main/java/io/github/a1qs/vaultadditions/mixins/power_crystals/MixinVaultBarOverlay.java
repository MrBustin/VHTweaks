package io.github.a1qs.vaultadditions.mixins.power_crystals;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.a1qs.vaultadditions.util.MiscUtil;
import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.client.render.hud.module.VaultPointsModule;
import iskallia.vault.client.render.hud.module.context.ModuleRenderContext;
import iskallia.vault.init.ModOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VaultPointsModule.class, remap = false)
public class MixinVaultBarOverlay {  //VaultBarOverlay was the old class, is called VaultPointsModule in 3.21

    @Inject(
            method = "renderModule(Liskallia/vault/client/render/hud/module/context/ModuleRenderContext;)V",
            at = @At("TAIL")
    )
    private void renderPowerPoints(ModuleRenderContext context, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        if (!vaultadditions$shouldRender(context)) {
            return;
        }

        MiscUtil.POWER_POINT_SUPPLIER.ifChanged(MixinVaultBarOverlay::vaultadditions$onUnspentPowerPointsChanged);

        PoseStack poseStack = context.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-5.0D, vaultadditions$getPotionOffset(player), 0.0D);

        int y = 2;
        if (VaultBarOverlay.unspentSkillPoints != 0) y += 12;
        if (VaultBarOverlay.unspentExpertisePoints != 0) y += 12;
        if (VaultBarOverlay.unspentKnowledgePoints != 0) y += 12;
        if (VaultBarOverlay.unspentArchetypePoints != 0) y += 12;

        int right = Math.max(vaultadditions$getVanillaWidth(), MiscUtil.unspentPowerPointComponentWidth) + 5;
        minecraft.font.drawShadow(
                poseStack,
                MiscUtil.unspentPowerPointComponent,
                (float) (right - MiscUtil.unspentPowerPointComponentWidth),
                (float) y,
                0xFFFFFF
        );

        poseStack.popPose();
    }

    @Inject(
            method = "baseWidth(Liskallia/vault/client/render/hud/module/context/ModuleRenderContext;)I",
            at = @At("RETURN"),
            cancellable = true
    )
    private void includePowerPointWidth(ModuleRenderContext context, CallbackInfoReturnable<Integer> cir) {
        if (vaultadditions$shouldRender(context)) {
            cir.setReturnValue(Math.max(cir.getReturnValue(), MiscUtil.unspentPowerPointComponentWidth + 5));
        }
    }

    @Inject(
            method = "baseHeight(Liskallia/vault/client/render/hud/module/context/ModuleRenderContext;)I",
            at = @At("RETURN"),
            cancellable = true
    )
    private void includePowerPointHeight(ModuleRenderContext context, CallbackInfoReturnable<Integer> cir) {
        if (vaultadditions$shouldRender(context)) {
            cir.setReturnValue(cir.getReturnValue() + 12);
        }
    }

    @Unique
    private static boolean vaultadditions$shouldRender(ModuleRenderContext context) {
        if ((!ModOptions.SHOW_POINT_MESSAGES.getValue()) && !context.isEditing()) {
            return false;
        }
        return MiscUtil.unspentPowerPoints != 0 && VaultBarOverlay.vaultLevel >= 100;
    }

    @Unique
    private static int vaultadditions$getVanillaWidth() {
        int width = 0;
        width = Math.max(width, VaultBarOverlay.unspentSkillPointComponentWidth);
        width = Math.max(width, VaultBarOverlay.unspentExpertisePointComponentWidth);
        width = Math.max(width, VaultBarOverlay.unspentKnowledgePointComponentWidth);
        width = Math.max(width, VaultBarOverlay.unspentArchetypePointComponentWidth);
        return width;
    }

    @Unique
    private static int vaultadditions$getPotionOffset(LocalPlayer player) {
        if (player == null || !ModOptions.VAULT_POINTS_OFFSET_FOR_POTIONS.getValue()) {
            return 0;
        }
        return VaultBarOverlay.potionOffsetY(player);
    }

    @Unique
    private static void vaultadditions$onUnspentPowerPointsChanged(int unspentPowerPoints) {
        MutableComponent cmp = new TextComponent(String.valueOf(unspentPowerPoints))
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16724414)));
        int absUnspentPowerPoint = Math.abs(unspentPowerPoints);
        MiscUtil.unspentPowerPointComponent = cmp.append(
                new TextComponent(" unspent power point" + (absUnspentPowerPoint == 1 ? "" : "s"))
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16777215)))
        );
        MiscUtil.unspentPowerPointComponentWidth = Minecraft.getInstance().font.width(MiscUtil.unspentPowerPointComponent);
    }
}