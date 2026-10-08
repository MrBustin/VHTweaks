package xyz.iwolfking.woldsvaults.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

public class WendarrSandTileEntity extends HellishSandTileEntity {
    public WendarrSandTileEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.WENDARR_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    protected int getParticleGreen() {
        return 203;
    }

    @Override
    protected int getParticleRed() {
        return 227;
    }
}
