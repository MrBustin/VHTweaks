package xyz.iwolfking.woldsvaults.entities.ghosts.lib.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.content.mobs.client.model.WraithModel;
import vazkii.quark.content.mobs.entity.Wraith;

public class GenericWraithRenderer extends MobRenderer<Wraith, WraithModel> {

    private final ResourceLocation TEXTURE;

    public GenericWraithRenderer(EntityRendererProvider.Context context, ResourceLocation textureLocation) {
        super(context, ModelHandler.model(ModelHandler.wraith), 0F);
        this.TEXTURE = textureLocation;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Wraith genericEffectWraith) {
        return TEXTURE;
    }


}
