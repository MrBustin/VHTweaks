package xyz.iwolfking.woldsvaults.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

public class TenosSandTileEntity extends HellishSandTileEntity {
    public TenosSandTileEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TENOS_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    protected int getParticleRed() {
        return 0;
    }

    @Override
    protected int getParticleBlue() {
        return 226;
    }

    @Override
    protected int getParticleGreen() {
        return 197;
    }
}
