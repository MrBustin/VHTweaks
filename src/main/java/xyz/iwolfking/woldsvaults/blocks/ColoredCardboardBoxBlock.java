package xyz.iwolfking.woldsvaults.blocks;

import iskallia.vault.block.CardboardBoxBlock;
import iskallia.vault.block.entity.CardboardBoxTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class ColoredCardboardBoxBlock extends CardboardBoxBlock {
    private final Supplier<BlockEntityType<CardboardBoxTileEntity>> blockEntityType;

    public ColoredCardboardBoxBlock(Supplier<BlockEntityType<CardboardBoxTileEntity>> blockEntityType) {
        this.blockEntityType = blockEntityType;
    }

    public BlockEntityType<CardboardBoxTileEntity> getBlockEntityType() {
        return blockEntityType.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntityType().create(pos, state);
    }
}
