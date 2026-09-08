package xyz.iwolfking.woldsvaults.entities.astral.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.entities.astral.AstralStalkerEntity;

public class AstralStalkerRenderer extends HumanoidMobRenderer<AstralStalkerEntity, SkeletonModel<AstralStalkerEntity>> {

    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png");

    public AstralStalkerRenderer(EntityRendererProvider.Context context) {

        super(context, new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON)), 0.5F);

        this.addLayer(new RenderLayer<>(this) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AstralStalkerEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.endPortal());
                this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 0.7F, 0.2F, 1.0F, 1.0F);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(AstralStalkerEntity entity) {
        return TEXTURE;
    }
}