package io.github.a1qs.vaultadditions.block.blockentity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.a1qs.vaultadditions.block.StatueCauldronBlock;
import io.github.a1qs.vaultadditions.block.blockentity.StatueCauldronBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.awt.*;

public class StatueCauldronRenderer implements BlockEntityRenderer<StatueCauldronBlockEntity> {

    public StatueCauldronRenderer (BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(StatueCauldronBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        BlockState state = blockEntity.getBlockState();
        int level = state.getValue(StatueCauldronBlock.LEVEL);
        float percentage = (float)blockEntity.getStatueCount() / (float)blockEntity.getRequiredAmount();
        int height = 14;
        if (level < 3) {
            if (level == 2) {
                this.renderLiquid(poseStack, multiBufferSource, 0.0F, percentage, 1.0F - percentage, height - 2);
            }
        } else {
            renderLabel(blockEntity, poseStack);
            this.renderLiquid(poseStack, multiBufferSource, 0.0F, percentage, 1.0F - percentage, height);
        }


    }

    private void renderLiquid(PoseStack matrixStack, MultiBufferSource buffer, float r, float g, float b, int height) {
        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(Fluids.WATER.getAttributes().getStillTexture());
        matrixStack.pushPose();
        this.addVertex(builder, matrixStack, this.p2f(1), this.p2f(height), this.p2f(1), sprite.getU0(), sprite.getV0(), r, g, b, 1.0F);
        this.addVertex(builder, matrixStack, this.p2f(1), this.p2f(height), this.p2f(15), sprite.getU1(), sprite.getV0(), r, g, b, 1.0F);
        this.addVertex(builder, matrixStack, this.p2f(15), this.p2f(height), this.p2f(15), sprite.getU1(), sprite.getV1(), r, g, b, 1.0F);
        this.addVertex(builder, matrixStack, this.p2f(15), this.p2f(height), this.p2f(1), sprite.getU0(), sprite.getV1(), r, g, b, 1.0F);
        matrixStack.popPose();
    }

    private void renderLabel(StatueCauldronBlockEntity blockEntity, PoseStack poseStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        HitResult hitResult = minecraft.hitResult;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos hitPos = ((BlockHitResult) hitResult).getBlockPos();
            if (!hitPos.equals(blockEntity.getBlockPos())) return;

            // Render text and background above the block
            poseStack.pushPose();
            poseStack.translate(0.5, 1.65, 0.5); // Adjust position above the block
            poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
            poseStack.scale(-0.025F, -0.025F, 0.025F); // Scale for text rendering

            // Slightly offset in Z-direction to prevent Z-fighting
            poseStack.translate(0, 0, -0.01);

            Font font = minecraft.font;
            float percentage = (float)blockEntity.getStatueCount() / (float)blockEntity.getRequiredAmount() *100;

            // Calculate color based on percentage
            int red = (int) (255 * (1 - percentage / 100));
            int green = (int) (255 * (percentage / 100));
            int color = (red << 16) | (green << 8);

            String staticText = "Filled: ";
            String percentageText = String.format("%.0f", percentage) + "%";

            float staticTextWidth = font.width(staticText);
            float percentageTextWidth = font.width(percentageText);
            float totalTextWidth = staticTextWidth + percentageTextWidth;

            float backgroundPadding = 4; // Padding around the text
            float backgroundWidth = totalTextWidth + backgroundPadding * 2;
            float backgroundHeight = 10; // Adjust height based on text size

            // Render background box
            RenderSystem.enableBlend(); // Enable transparency
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest(); // Ensure it renders above everything
            GuiComponent.fill(
                    poseStack,
                    (int) -backgroundWidth / 2,
                    (int) -backgroundHeight / 2,
                    (int) backgroundWidth / 2,
                    (int) backgroundHeight / 2,
                    1711276032 // Semi-transparent black
            );

            // Render the "Filled:" text in white
            font.draw(poseStack, staticText, -totalTextWidth / 2, -4, 0xFFFFFF);

            // Render the percentage text with gradient color
            font.draw(poseStack, percentageText, -totalTextWidth / 2 + staticTextWidth, -4, color);

            // Restore render settings
            RenderSystem.enableDepthTest(); // Re-enable depth test for other elements
            RenderSystem.disableBlend(); // Disable transparency blending

            // Pop pose stack
            poseStack.popPose();
        }
    }


    private void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v, float r, float g, float b, float a) {
        renderer.vertex(stack.last().pose(), x, y, z)
                .color(r, g, b, 0.5F)
                .uv(u, v)
                .overlayCoords(0, 10)
                .uv2(0, 240)
                .normal(1.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private float p2f(int pixel) {
        return 0.0625F * (float)pixel;
    }

    public static Color getBlendedColor(float percentage) {
        float green = ensureRange(percentage);
        float blue = ensureRange(1.0F - percentage);
        return new Color(0.0F, green, blue);
    }

    private static float ensureRange(float value) {
        return Math.min(Math.max(value, 0.0F), 1.0F);
    }
}
