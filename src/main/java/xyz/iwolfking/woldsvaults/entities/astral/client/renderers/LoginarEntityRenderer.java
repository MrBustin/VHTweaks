package xyz.iwolfking.woldsvaults.entities.astral.client.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.entities.astral.LoginarHostileAlienEntity;
import xyz.iwolfking.woldsvaults.entities.astral.client.model.LoginarAntennaeLayer;
import xyz.iwolfking.woldsvaults.entities.astral.client.model.LoginarModel;

public class LoginarEntityRenderer extends MobRenderer<LoginarHostileAlienEntity, LoginarModel> {
    private static final ResourceLocation TEXTURE = WoldsVaults.id("textures/entity/loginar.png");

    public LoginarEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LoginarModel(context.bakeLayer(LoginarModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new LoginarAntennaeLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(LoginarHostileAlienEntity entity) {
        return TEXTURE;
    }
}