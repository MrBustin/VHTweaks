package xyz.iwolfking.woldsvaults.entities.astral.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.entities.astral.StarDevourerEntity;
import xyz.iwolfking.woldsvaults.entities.astral.client.model.StarDevourerModel;

public class StarDevourerRenderer extends MobRenderer<StarDevourerEntity, StarDevourerModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/ravager.png");

    public StarDevourerRenderer(EntityRendererProvider.Context context) {
        super(context, new StarDevourerModel(context.bakeLayer(ModelLayers.RAVAGER)), 1.1F);
    }

    @Override
    public ResourceLocation getTextureLocation(StarDevourerEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(StarDevourerEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        this.addLayer(new RenderLayer<>(this) {
            @Override
            public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, StarDevourerEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
                VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.endPortal());
                this.getParentModel().renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0.0F), 0.7F, 0.2F, 1.0F, 1.0F);
            }
        });
    }
}