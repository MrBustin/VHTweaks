package xyz.iwolfking.woldsvaults.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

public class IdonaSandTileEntity extends HellishSandTileEntity {
    public IdonaSandTileEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.IDONA_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
    }
}
