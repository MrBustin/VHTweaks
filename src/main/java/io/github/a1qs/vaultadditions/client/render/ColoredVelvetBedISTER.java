package io.github.a1qs.vaultadditions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.a1qs.vaultadditions.block.ColoredVelvetBed;
import io.github.a1qs.vaultadditions.block.blockentity.ColoredVelvetBedBlockEntity;
import io.github.a1qs.vaultadditions.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.util.NonNullLazy;

public class ColoredVelvetBedISTER extends BlockEntityWithoutLevelRenderer {
    public static final IItemRenderProperties INSTANCE = new IItemRenderProperties() {
        final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(
                () -> new ColoredVelvetBedISTER(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                        Minecraft.getInstance().getEntityModels()));

        @Override
        public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return renderer.get();
        }
    };

    private final BlockEntityRenderDispatcher blockEntityRenderer;


    public ColoredVelvetBedISTER(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
        this.blockEntityRenderer = pBlockEntityRenderDispatcher;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType type, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (!(stack.getItem() instanceof BedItem bedItem)) {
            return;
        }

        // Get the block associated with this bed item
        Block bedBlock = bedItem.getBlock();

        if (bedBlock instanceof ColoredVelvetBed coloredVelvetBed) {
            BlockState bedState = coloredVelvetBed.defaultBlockState();
            ColoredVelvetBedBlockEntity bedEntity = new ColoredVelvetBedBlockEntity(BlockPos.ZERO, bedState);

            this.blockEntityRenderer.renderItem(bedEntity, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        }
    }

}
