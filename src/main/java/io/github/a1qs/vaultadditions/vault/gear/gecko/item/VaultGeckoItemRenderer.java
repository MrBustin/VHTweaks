package io.github.a1qs.vaultadditions.vault.gear.gecko.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.a1qs.vaultadditions.util.ModelUtil;
import io.github.a1qs.vaultadditions.vault.gear.gecko.VaultGeckoModel;
import io.github.a1qs.vaultadditions.vault.gear.gecko.VaultGeckoModelProvider;
import iskallia.vault.gear.item.VaultGearItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class VaultGeckoItemRenderer<T extends Item & VaultGearItem & IAnimatable> extends GeoItemRenderer<T> {
    private final IItemRenderProperties defaultProperties;

    public VaultGeckoItemRenderer(IItemRenderProperties defaultProperties) {
        super(new VaultGeckoModelProvider<>());
        this.defaultProperties = defaultProperties != null ? defaultProperties : IItemRenderProperties.DUMMY;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!(ModelUtil.getDynamicModel(stack) instanceof VaultGeckoModel)) {
            defaultProperties.getItemStackRenderer().renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
            return;
        }
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void render(T animatable, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ItemStack stack) {
        ((VaultGeckoModelProvider<?>) getGeoModelProvider()).using(stack);
        super.render(animatable, poseStack, bufferSource, packedLight, stack);
    }

    @Override
    public ResourceLocation getTextureLocation(T animatable) {
        return ((VaultGeckoModelProvider<?>) getGeoModelProvider()).getTextureLocation(this.currentItemStack);
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}
