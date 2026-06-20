package io.github.a1qs.vaultadditions.block.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import io.github.a1qs.vaultadditions.block.blockentity.EventBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class EventBlockRenderer implements BlockEntityRenderer<EventBlockEntity> {

    public EventBlockRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(EventBlockEntity eventBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLightIn, int combinedOverlayIn) {
        List<String> lines = eventBlockEntity.getLines();
        int length = lines.size();
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;

        for(int i = length - 1; i >= 0; --i) {
            String line = lines.get(i);
            MutableComponent text = Component.Serializer.fromJsonLenient(line);
            if (text != null) {
                float scale = 0.02F;
                int color = -1;
                int opacity = 1711276032;
                poseStack.pushPose();
                Matrix4f matrix4f = poseStack.last().pose();
                float offset = (float)(-fontRenderer.width(text) / 2);
                poseStack.translate(0.5, (1.7F + 0.25F * (float)(length - i)), 0.5);
                poseStack.scale(scale, scale, scale);
                poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                fontRenderer.drawInBatch(text, offset, 0.0F, color, false, matrix4f, multiBufferSource, true, opacity, combinedLightIn);
                fontRenderer.drawInBatch(text, offset, 0.0F, -1, false, matrix4f, multiBufferSource, false, 0, combinedLightIn);
                poseStack.popPose();
            }
        }
    }
}
