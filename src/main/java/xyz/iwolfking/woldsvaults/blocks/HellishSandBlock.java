package xyz.iwolfking.woldsvaults.blocks;

import iskallia.vault.block.base.LootableBlock;
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
import java.util.function.Supplier;

public class HellishSandBlock extends LootableBlock {
    private final Supplier<BlockEntityType<? extends HellishSandTileEntity>> blockEntityType;

    public HellishSandBlock() {
        this(() -> ModBlocks.HELLISH_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE);
    }

    public HellishSandBlock(Supplier<BlockEntityType<? extends HellishSandTileEntity>> blockEntityType) {
        super(Properties.copy(Blocks.RED_SAND));
        this.blockEntityType = blockEntityType;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level world, BlockState state, BlockEntityType<A> type) {
        if (!world.isClientSide() || type != blockEntityType.get()) {
            return null;
        }

        BlockEntityTicker<HellishSandTileEntity> ticker = HellishSandTileEntity::tick;
        return (BlockEntityTicker<A>)(BlockEntityTicker<?>)ticker;
    }

    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return blockEntityType.get().create(pos, state);
    }
}
