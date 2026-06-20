package io.github.a1qs.vaultadditions.block.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import io.github.a1qs.vaultadditions.block.blockentity.GlobeExpanderBlockEntity;
import io.github.a1qs.vaultadditions.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GlobeExpanderEntityRenderer implements BlockEntityRenderer<GlobeExpanderBlockEntity> {

    public GlobeExpanderEntityRenderer(BlockEntityRendererProvider.Context context) {}


    @Override
    public void render(GlobeExpanderBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (!blockEntity.shouldAnimate()) {
            return;  // If it's not animating, don't render anything.
        }

        Level world = blockEntity.getLevel();
        if (world != null) {
            ItemStack itemStack = new ItemStack(ModItems.POWER_CRYSTAL.get());

            matrixStack.pushPose();
            this.renderItem(matrixStack, bufferSource, combinedLight, combinedOverlay, 1.5F, 0.65F, itemStack, blockEntity, partialTicks);
            matrixStack.popPose();
        }

    }

    private void renderItem(PoseStack matrixStack, MultiBufferSource buffer, int lightLevel, int overlay, float yOffset, float scale, ItemStack itemStack, GlobeExpanderBlockEntity blockEntity, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        matrixStack.pushPose();

        matrixStack.translate(0.5, yOffset, 0.5);
        matrixStack.scale(scale, scale, scale);

        // Use ticks for smooth animation
        float lerp = Mth.lerp(partialTicks, blockEntity.ticksSpinningOld, blockEntity.ticksSpinning);

        // Progress of the animation (0.0 at start, 1.0 at end)
        float progress = lerp / (float) blockEntity.maxSpinTicks;
        float acceleratedProgress = (0.1f + progress) * progress * progress;
        float baseRotationSpeed = 20.0F;
        float rotationDegrees = (acceleratedProgress * baseRotationSpeed * lerp) % 360;

        matrixStack.mulPose(Quaternion.fromXYZ(0.0F, (float) Math.toRadians(rotationDegrees), 0.0F));

        BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
        minecraft.getItemRenderer().render(itemStack, ItemTransforms.TransformType.FIXED, true, matrixStack, buffer, lightLevel, overlay, bakedModel);

        matrixStack.popPose();
    }
}



