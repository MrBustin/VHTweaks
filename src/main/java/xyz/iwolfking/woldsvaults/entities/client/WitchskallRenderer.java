package xyz.iwolfking.woldsvaults.entities.client;

import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.entities.WitchskallEntity;

public class WitchskallRenderer extends MobRenderer<WitchskallEntity, WitchModel<WitchskallEntity>> {
    private static final ResourceLocation TEXTURE = WoldsVaults.id("textures/entity/witchskall.png");

    public WitchskallRenderer(EntityRendererProvider.Context context) {
        super(context, new WitchModel<>(context.bakeLayer(ModelLayers.WITCH)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(WitchskallEntity witchskall) {
        return TEXTURE;
    }
}
