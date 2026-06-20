package xyz.iwolfking.woldsvaults.blocks.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import xyz.iwolfking.woldsvaults.blocks.tiles.DecoGridGatewayTileEntity;

public class DecoGridGatewayRenderer implements BlockEntityRenderer<DecoGridGatewayTileEntity> {
    private final BlockRenderDispatcher brd;

    public DecoGridGatewayRenderer(BlockEntityRendererProvider.Context context) {
        this.brd = context.getBlockRenderDispatcher();
    }

    public void render(DecoGridGatewayTileEntity tile, float pPartialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        BlockState state = tile.getBlockState();
        BakedModel bakedmodel = this.brd.getBlockModel(state);
        VertexConsumer buf = buffers.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
        bakedmodel.getQuads(state, (Direction) null, tile.getLevel().getRandom(), EmptyModelData.INSTANCE).forEach((quad) -> {
            this.putQuadData(tile, buf, poseStack.last(), quad, packedLight, packedOverlay);
        });
    }

    private void putQuadData(DecoGridGatewayTileEntity tile, VertexConsumer vertexConsumer, PoseStack.Pose pose, BakedQuad quad, int packedLight, int pPackedOverlay) {
        float red;
        float green;
        float blue;
        if (quad.isTinted()) {
            int i = tile.getTintColor(quad.getTintIndex(), tile);
            red = (float) (i >> 16 & 255) / 255.0F;
            green = (float) (i >> 8 & 255) / 255.0F;
            blue = (float) (i & 255) / 255.0F;
        } else {
            red = 1.0F;
            green = 1.0F;
            blue = 1.0F;
        }

        vertexConsumer.putBulkData(pose, quad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, new int[]{packedLight, packedLight, packedLight, packedLight}, pPackedOverlay, true);
    }

}
