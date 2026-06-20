package xyz.iwolfking.woldsvaults.items.gear.rang;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class VaultRangRenderer extends EntityRenderer<VaultRangEntity> {
    public VaultRangRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(VaultRangEntity vaultRangEntity) {
        return null;
    }

    @Override
    public void render(VaultRangEntity entity, float yaw, float partialTicks, PoseStack matrix, @Nonnull MultiBufferSource buffer, int light) {
        matrix.pushPose();
        matrix.translate(0, 0.2, 0);
        matrix.mulPose(Vector3f.XP.rotationDegrees(90F));

        Minecraft mc = Minecraft.getInstance();
        float time = entity.tickCount + (mc.isPaused() ? 0 : partialTicks);
        matrix.mulPose(Vector3f.ZP.rotationDegrees(time * 20F));

        mc.getItemRenderer().renderStatic(entity.getStack(), ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrix, buffer, 0);

        matrix.popPose();
    }
}
