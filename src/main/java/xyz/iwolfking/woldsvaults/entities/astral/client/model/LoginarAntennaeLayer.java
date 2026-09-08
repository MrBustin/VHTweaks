package xyz.iwolfking.woldsvaults.entities.astral.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import xyz.iwolfking.woldsvaults.WoldsVaults;

import java.awt.*;

public class LoginarAntennaeLayer<T extends Entity, M extends EntityModel<T>> extends EyesLayer<T, M> {
    private static final RenderType LOGINAR_ANTENNAE = RenderType.eyes(WoldsVaults.id("textures/entity/loginar_antennae.png"));

    public LoginarAntennaeLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public RenderType renderType() {
        return LOGINAR_ANTENNAE;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource multiBufferSource, int par3, T entity, float par5, float par6, float par7, float par8, float par9, float par10) {
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(this.renderType());
        Color color = new Color(0x99FFFF);
        this.getParentModel().renderToBuffer(stack,
                vertexconsumer,
                15728640,
                OverlayTexture.NO_OVERLAY,
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                1.0F
        );
    }
}