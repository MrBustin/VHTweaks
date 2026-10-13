package xyz.iwolfking.woldsvaults.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.entity.model.FireballModel;
import iskallia.vault.entity.renderer.FireballRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import xyz.iwolfking.woldsvaults.entities.msc.VaultMeteor;

public class VaultMeteorRenderer extends EntityRenderer<VaultMeteor> {
    private final FireballModel model;

    public VaultMeteorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new FireballModel(context.bakeLayer(FireballModel.MODEL_LOCATION));
    }

    @Override
    public void render(VaultMeteor meteor, float yaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(
                Mth.lerp(partialTicks, meteor.yRotO, meteor.getYRot()) - 90.0F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(
                Mth.lerp(partialTicks, meteor.xRotO, meteor.getXRot()) + 90.0F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(
                Mth.lerp(partialTicks, (meteor.tickCount - 1) * 15.0F, meteor.tickCount * 15.0F)));
        poseStack.scale(1.1F, 1.1F, 1.1F);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(meteor)));
        this.model.renderToBuffer(poseStack, consumer, 0xF000F0, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        super.render(meteor, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(VaultMeteor meteor) {
        return FireballRenderer.FIREBALL;
    }
}
