package xyz.iwolfking.woldsvaults.blocks;

import iskallia.vault.block.base.LootableBlock;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xyz.iwolfking.woldsvaults.blocks.tiles.HellishSandTileEntity;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HellishSandBlock extends LootableBlock {
    public HellishSandBlock() {
        super(Properties.copy(Blocks.RED_SAND));
    }

    @Nullable
    public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level world, BlockState state, BlockEntityType<A> type) {
        return !world.isClientSide() ? null : BlockHelper.getTicker(type, ModBlocks.HELLISH_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE, HellishSandTileEntity::tick);
    }

    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return ModBlocks.HELLISH_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE.create(pos, state);
    }
}
