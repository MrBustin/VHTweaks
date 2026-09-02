package xyz.iwolfking.woldsvaults.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.entity.renderer.dungeon.DungeonWitchRenderer;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.entities.VaultNecromancer;

public class VaultNecromancerRenderer extends MobRenderer<VaultNecromancer, WitchModel<VaultNecromancer>> {
    public VaultNecromancerRenderer(EntityRendererProvider.Context context) {
        super(context, new WitchModel<>(context.bakeLayer(ModelLayers.WITCH)), 0.5F);
    }

    @Override
    public void render(VaultNecromancer entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.model.setHoldingItem(false);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(VaultNecromancer entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTextureLocation(VaultNecromancer entity) {
        return DungeonWitchRenderer.TEXTURE;
    }
}
