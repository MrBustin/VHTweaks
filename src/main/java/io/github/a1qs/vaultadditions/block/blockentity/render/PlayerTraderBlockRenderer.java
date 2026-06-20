package io.github.a1qs.vaultadditions.block.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import io.github.a1qs.vaultadditions.block.blockentity.PlayerTraderBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

public class PlayerTraderBlockRenderer implements BlockEntityRenderer<PlayerTraderBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final Font font;

    public PlayerTraderBlockRenderer(BlockEntityRendererProvider.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        this.itemRenderer = minecraft.getItemRenderer();
        this.font = minecraft.gui.getFont();

    }


    public static void renderName(Component name, float h, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn) {
        Minecraft mc = Minecraft.getInstance();

        int i = 0;
        poseStack.pushPose();

        poseStack.translate(0, h, 0);
        poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = poseStack.last().pose();
        float f1 = mc.options.getBackgroundOpacity(0.25F);
        int j = (int) (f1 * 255.0F) << 24;

        float f2 = (float) (-mc.font.width(name) / 2);

        mc.font.drawInBatch(name, f2, (float) i, -1, false, matrix4f, bufferIn, false, j, combinedLightIn);
        poseStack.popPose();
    }

    @Override
    public void render(PlayerTraderBlockEntity tile, float partialTicks, PoseStack pPoseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        ItemStack offerStack = tile.getOffer();
        if (!offerStack.isEmpty()) {
            ItemStack currency = tile.getCurrencyStack();

            pPoseStack.pushPose();
            pPoseStack.translate(0.5, 0.5, 0.5);

            drawPrice(currency, pPoseStack, buffer, String.valueOf(currency.getCount()), combinedLightIn, combinedOverlayIn);

            pPoseStack.pushPose();
            pPoseStack.translate(0, 0.625, 0);

            final Component[] hoverName = {offerStack.getHoverName()};
            renderName(hoverName[0], 0.875f, pPoseStack, buffer, combinedLightIn);

            pPoseStack.scale(0.5f, 0.5f, 0.5f);
            pPoseStack.translate(0, 0.25, 0);

            ItemTransforms.TransformType transform = ItemTransforms.TransformType.FIXED;

            pPoseStack.translate(0, 6 / 16f, 0);
            pPoseStack.scale(1.5f, 1.5f, 1.5f);

            long time = tile.getLevel().getGameTime();

            int scale = (int) (1 * 360f);
            float angle = ((float) Math.floorMod(time, (long) scale) + partialTicks) / (float) scale;
            Quaternion rotation = Vector3f.YP.rotation((float) (angle * Math.PI * 10));
            pPoseStack.mulPose(rotation);


            this.itemRenderer.renderStatic(offerStack, transform, combinedLightIn, combinedOverlayIn, pPoseStack, buffer, 0);



            if (offerStack.getCount() > 1) {
                String countText = String.valueOf(offerStack.getCount());
                pPoseStack.pushPose();

                pPoseStack.scale(0.06f, 0.06f, 0.06f);
                pPoseStack.mulPose(Vector3f.ZP.rotation((float)Math.PI));
                pPoseStack.translate(-29.5D, -28.5D, -0.7D);

                this.font.drawInBatch(countText, (float)(20 + 19 - 2 - this.font.width(countText)), (float)(20 + 6 + 3), 16777215, true, pPoseStack.last().pose(), buffer, false, combinedLightIn, combinedLightIn);


                pPoseStack.mulPose(Vector3f.YN.rotation((float)Math.PI));
                pPoseStack.translate(-59.5D, -0.0D, -1.4D);

                this.font.drawInBatch(countText, (float)(20 + 19 - 2 - this.font.width(countText)), (float)(20 + 6 + 3), 16777215, true, pPoseStack.last().pose(), buffer, false, combinedLightIn, combinedLightIn);

                pPoseStack.popPose();
            }

            pPoseStack.popPose();
            pPoseStack.popPose();
        }
    }

    private void drawPrice(ItemStack stack, PoseStack matrixStack, MultiBufferSource buffer, String name, int combinedLight, int combinedOverlay) {
        FormattedCharSequence text = new TextComponent(name).getVisualOrderText();
        Font fr = font;
        int xOffset = fr.width(text);


        for (Direction dir : Direction.Plane.HORIZONTAL) {
            matrixStack.pushPose();

            matrixStack.mulPose(Vector3f.YP.rotationDegrees(dir.toYRot()));
            matrixStack.translate(0, 0, 7/16f);

            matrixStack.pushPose();
            matrixStack.translate(0,-0.16, 0.075);
            matrixStack.scale(0.6125f, 0.6125f, 0.6125f);
            itemRenderer.renderStatic(stack, ItemTransforms.TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer, 0);
            matrixStack.popPose();


            matrixStack.translate(0, 0.36, 0.075);
            float scale  = 0.015f;
            matrixStack.scale(scale, -scale, scale);

            fr.drawInBatch(text, 1-xOffset / 2F, 1-fr.lineHeight / 2f, 0xFF000000,
                    false, matrixStack.last().pose(), buffer,
                    false, 0, combinedLight);

            matrixStack.translate(0,0, 0.001);
            fr.drawInBatch(text, -xOffset / 2F, -fr.lineHeight / 2f, -1,
                    false, matrixStack.last().pose(), buffer,
                    false, 0, combinedLight);

            matrixStack.popPose();
        }
    }


}