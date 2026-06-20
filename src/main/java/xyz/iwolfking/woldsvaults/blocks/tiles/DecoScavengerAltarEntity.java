package xyz.iwolfking.woldsvaults.blocks.tiles;

import iskallia.vault.block.entity.ScavengerAltarTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

public class DecoScavengerAltarEntity extends ScavengerAltarTileEntity {

    public DecoScavengerAltarEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DECO_SCAVENGER_ALTAR_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
        this.ticksToConsume = 40;
        this.consuming = false;
    }

    public static void tickClient(Level world, BlockPos pos, BlockState state, DecoScavengerAltarEntity tile) {
        ScavengerAltarTileEntity.tickClient(world, pos, state, tile);
    }
}
