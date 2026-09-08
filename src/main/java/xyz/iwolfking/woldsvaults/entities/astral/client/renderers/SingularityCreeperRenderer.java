package xyz.iwolfking.woldsvaults.entities.astral.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.entities.astral.SingularityCreeperEntity;

public class SingularityCreeperRenderer extends MobRenderer<SingularityCreeperEntity, CreeperModel<SingularityCreeperEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper.png");

    public SingularityCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperModel<>(context.bakeLayer(ModelLayers.CREEPER)), 0.3F);

        this.addLayer(new RenderLayer<>(this) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SingularityCreeperEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.endPortal());
                this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 0.7F, 0.2F, 1.0F, 1.0F);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(SingularityCreeperEntity entity) {
        return TEXTURE;
    }
}