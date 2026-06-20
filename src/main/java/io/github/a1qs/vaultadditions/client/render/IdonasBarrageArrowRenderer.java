package io.github.a1qs.vaultadditions.client.render;

import io.github.a1qs.vaultadditions.entity.IdonasBarrageArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class IdonasBarrageArrowRenderer extends ArrowRenderer<IdonasBarrageArrowEntity> {
    public IdonasBarrageArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull IdonasBarrageArrowEntity entity) {
        return TippableArrowRenderer.NORMAL_ARROW_LOCATION;
    }
}
