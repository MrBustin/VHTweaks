package xyz.iwolfking.woldsvaults.core.vault.challenge.temple;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.gui.helper.LightmapHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TempleOverlayAnimation {
private static final int DURATION = 40;
    private boolean active;
    private int ticker;

    public void onStart() {
        this.active = true;
        this.ticker = 0;
    }

    public void onTick() {
        if (this.active && ++this.ticker > DURATION) {
            this.active = false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onRender(PoseStack poseStack, float partialTicks, Window window) {
        if (!this.active) return;
        Minecraft minecraft = Minecraft.getInstance();
        Component text = new TextComponent("Temple Activated");
        int alpha = (int) (255 * (1.0F - (this.ticker + partialTicks) / DURATION)) << 24;
        int midX = window.getGuiScaledWidth() / 2;
        Font font = minecraft.font;
        MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        poseStack.pushPose();
        poseStack.scale(0.6F, 0.6F, 0.6F);
        font.drawInBatch(text.getVisualOrderText(), midX / 0.6F - (font.width(text) / 2f),
                font.lineHeight, alpha | 0xFFFFFF, true, poseStack.last().pose(), buffer,
                false, 0, LightmapHelper.getPackedFullbrightCoords());
        buffer.endBatch();
        poseStack.popPose();
    }
}