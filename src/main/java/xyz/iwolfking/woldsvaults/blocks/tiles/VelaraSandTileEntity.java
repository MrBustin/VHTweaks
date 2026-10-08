package xyz.iwolfking.woldsvaults.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

public class VelaraSandTileEntity extends HellishSandTileEntity {
    public VelaraSandTileEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.VELARA_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    protected int getParticleRed() {
        return 0;
    }

    @Override
    protected int getParticleGreen() {
        return 255;
    }
}
