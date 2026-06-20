package xyz.iwolfking.woldsvaults.blocks.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import xyz.iwolfking.woldsvaults.blocks.tiles.SurvivalMobBarrierTileEntity;

import javax.annotation.Nonnull;

public class SurvivalMobBarrierRenderer implements BlockEntityRenderer<SurvivalMobBarrierTileEntity> {
    private final Minecraft mc = Minecraft.getInstance();

    public SurvivalMobBarrierRenderer(BlockEntityRendererProvider.Context context) {
    }

    public void render(SurvivalMobBarrierTileEntity mobBarrierTile, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Level world = mobBarrierTile.getLevel();
        if (world != null) {
            if (this.mc.player != null && (this.mc.player.isCreative() || this.mc.player.isSpectator())) {
                double position = (double)((this.mc.player.tickCount * 20 + (int)(partialTicks * 20.0F)) % 2000) / 2000.0;
                boolean colorDuration = true;
                int red = (int)(Math.cos(position * Math.PI * 2.0) * 127.0 + 128.0);
                int green = (int)(Math.cos((position + 0.3333333333333333) * Math.PI * 2.0) * 127.0 + 128.0);
                int blue = (int)(Math.cos((position + 0.6666666666666666) * Math.PI * 2.0) * 127.0 + 128.0);

                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.lines());
                LevelRenderer.renderLineBox(matrixStack, vertexconsumer, 0.10000000149011612, 0.10000000149011612, 0.10000000149011612, 0.8999999761581421, 0.8999999761581421, 0.8999999761581421, (float)red, (float)green, (float)blue, 1.0F, (float)red, (float)green, (float)blue);
            }

        }
    }

}
