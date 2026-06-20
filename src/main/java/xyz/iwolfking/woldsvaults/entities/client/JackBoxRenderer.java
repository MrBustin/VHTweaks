package xyz.iwolfking.woldsvaults.entities.client;


import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import xyz.iwolfking.woldsvaults.entities.aprilfools.JackBoxEntity;

public class JackBoxRenderer extends GeoEntityRenderer<JackBoxEntity> {
    public JackBoxRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new JackBoxModel());
        this.shadowRadius = 0.4F;
    }
}
