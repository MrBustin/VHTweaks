package xyz.iwolfking.woldsvaults.blocks.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.block.model.PylonCrystalModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import xyz.iwolfking.woldsvaults.blocks.tiles.DecoLodestoneTileEntity;

import javax.annotation.Nonnull;

public class DecoLodestoneRenderer implements BlockEntityRenderer<DecoLodestoneTileEntity> {
    protected final PylonCrystalModel crystalModel;

    public DecoLodestoneRenderer(BlockEntityRendererProvider.Context context) {
        this.crystalModel = new PylonCrystalModel(context.bakeLayer(PylonCrystalModel.MODEL_LOCATION));
    }

    public void render(@Nonnull DecoLodestoneTileEntity tileEntity, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int packetLight, int packetOverlay) {
        int a = tileEntity.isConsumed() ? 32 : 255;
        VertexConsumer vertexConsumer = PylonCrystalModel.MATERIAL.buffer(bufferSource, RenderType::entityTranslucent);
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Vector3f.ZP.rotation(3.1415927F));
        this.crystalModel.setupAnimations();
        this.crystalModel.renderToBuffer(poseStack, vertexConsumer, packetLight, packetOverlay, 1.0F, 1.0F, 1.0F, (float)a / 255.0F);
        poseStack.popPose();
    }
}
