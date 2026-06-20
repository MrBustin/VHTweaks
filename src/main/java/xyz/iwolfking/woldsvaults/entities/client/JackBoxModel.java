package xyz.iwolfking.woldsvaults.entities.client;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.entities.aprilfools.JackBoxEntity;

public class JackBoxModel extends AnimatedGeoModel<JackBoxEntity> {

    @Override
    public ResourceLocation getModelLocation(JackBoxEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(WoldsVaults.MODID, "geo/jack_box.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(JackBoxEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(WoldsVaults.MODID, "textures/entity/jack_box.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(JackBoxEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(WoldsVaults.MODID, "animations/jack_box.animation.json");
    }
}
