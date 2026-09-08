package xyz.iwolfking.woldsvaults.entities.astral.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.entities.astral.NebulaSentinelEntity;

public class NebulaSentinelRenderer extends MobRenderer<NebulaSentinelEntity, IronGolemModel<NebulaSentinelEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/iron_golem/iron_golem.png");
    private static final ResourceLocation SHIELD = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");

    public NebulaSentinelRenderer(EntityRendererProvider.Context context) {
        super(context, new IronGolemModel<>(context.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
        this.addLayer(new RenderLayer<>(this) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, NebulaSentinelEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.endPortal());
                this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight,  LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 0.8F, 0.4F, 1.0F, 1.0F);
            }
        });
        this.addLayer(new EnergySwirlLayer<>(this) {

            @Override
            protected float xOffset(float ageInTicks) {
                return ageInTicks * 0.01F;
            }

            @Override
            protected ResourceLocation getTextureLocation() {
                return SHIELD;
            }

            @Override
            protected IronGolemModel<NebulaSentinelEntity> model() {
                return new IronGolemModel<>(context.bakeLayer(ModelLayers.IRON_GOLEM));
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(NebulaSentinelEntity entity) {
        return TEXTURE;
    }
}