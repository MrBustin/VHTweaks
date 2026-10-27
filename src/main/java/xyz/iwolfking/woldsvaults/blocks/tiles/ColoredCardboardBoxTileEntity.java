package xyz.iwolfking.woldsvaults.blocks.tiles;

import iskallia.vault.block.entity.CardboardBoxTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xyz.iwolfking.woldsvaults.blocks.ColoredCardboardBoxBlock;

public class ColoredCardboardBoxTileEntity extends CardboardBoxTileEntity {
    public ColoredCardboardBoxTileEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        // The parent constructor hardcodes the original box type; use this variant for saving and validation.
        return ((ColoredCardboardBoxBlock) getBlockState().getBlock()).getBlockEntityType();
    }
}
