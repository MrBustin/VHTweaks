package xyz.iwolfking.woldsvaults.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.VaultStormArrow;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.entities.msc.VaultMeteor;
import xyz.iwolfking.woldsvaults.entities.msc.VaultMeteorArrow;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class MeteorArrowRenderer extends ArrowRenderer<VaultMeteorArrow> {
        public static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/storm_arrow.png");
        public static final ResourceLocation TEXTURE_LOCATION_BLIZZARD = VaultMod.id("textures/entity/blizzard_shard.png");

    public MeteorArrowRenderer(EntityRendererProvider.Context context) {

        super(context);
    }

        @Nonnull
        public ResourceLocation getTextureLocation(@Nonnull VaultMeteorArrow entity) {

        if(entity.getMeteorArrowType() == VaultMeteorArrow.MeteorType.BASE)
            return TEXTURE_LOCATION;
        else
            return TEXTURE_LOCATION_BLIZZARD;

    }

        @ParametersAreNonnullByDefault
        @Override
        public void render(VaultMeteorArrow entity, float pEntityYaw, float partialTicks, PoseStack pMatrixStack, MultiBufferSource
        pBuffer, int pPackedLight) {

        super.render(entity, pEntityYaw, partialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}
