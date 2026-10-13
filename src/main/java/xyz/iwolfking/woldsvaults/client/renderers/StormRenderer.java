package xyz.iwolfking.woldsvaults.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.iwolfking.woldsvaults.entities.msc.VaultMeteorStormEntity;



@OnlyIn(Dist.CLIENT)
public class StormRenderer extends EntityRenderer<VaultMeteorStormEntity> {

    public StormRenderer(EntityRendererProvider.Context p_174420_) {
        super(p_174420_);
    }

    public void render(VaultMeteorStormEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {

    }
    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(VaultMeteorStormEntity pEntity) {
        return null;
    }
}
