package xyz.iwolfking.woldsvaults.entities.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.entities.CrystalVendorEntity;


public class CrystalVendorRenderer extends MobRenderer<CrystalVendorEntity, VillagerModel<CrystalVendorEntity>> {
    private static final ResourceLocation TEXTURE = WoldsVaults.id("textures/entity/crystal_trader.png");


    public CrystalVendorRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
    }

    @Override
    public void render(CrystalVendorEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CrystalVendorEntity crystalVendorEntity) {
        return TEXTURE;
    }
}
