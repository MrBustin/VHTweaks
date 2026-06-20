package xyz.iwolfking.woldsvaults.blocks.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xyz.iwolfking.woldsvaults.init.ModBlocks;


public class SurvivalMobBarrierTileEntity extends BlockEntity {
    public SurvivalMobBarrierTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlocks.SURVIVAL_MOB_BARRIER_TILE_ENTITY_BLOCK_ENTITY_TYPE, pWorldPosition, pBlockState);
    }


}
