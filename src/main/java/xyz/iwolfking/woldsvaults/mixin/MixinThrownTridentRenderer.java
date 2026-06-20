package xyz.iwolfking.woldsvaults.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.items.gear.VaultTridentItem;

import javax.annotation.Nonnull;

@Mixin(ThrownTridentRenderer.class)
public abstract class MixinThrownTridentRenderer extends EntityRenderer<ThrownTrident> {
    protected MixinThrownTridentRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", cancellable = true)
    private void render(ThrownTrident trident, float yaw, float partialTicks, PoseStack matrix, @Nonnull MultiBufferSource buffer, int light, CallbackInfo ci) {
        ItemStack itemStack = ((ThrownTridentAccessor) trident).getTridentItem();
        if (itemStack.getItem() instanceof VaultTridentItem)  {
            matrix.pushPose();
            matrix.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, trident.yRotO, trident.getYRot()) - 90.0F));
            matrix.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, trident.xRotO, trident.getXRot()) - 135.0F));
            Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrix, buffer, 0);
            matrix.popPose();
            super.render(trident, yaw, partialTicks, matrix, buffer, light);
            ci.cancel();
        }
    }
}