package xyz.iwolfking.woldsvaults.blocks.tiles;

import xyz.iwolfking.woldsvaults.init.additions.ModBlockEntities;
import iskallia.vault.block.entity.SoulPlaqueTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RaidPlaqueTileEntity extends SoulPlaqueTileEntity {
    public RaidPlaqueTileEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RAID_PLAQUE_BLOCK_ENTITY.get(), pos, state);
    }
}
