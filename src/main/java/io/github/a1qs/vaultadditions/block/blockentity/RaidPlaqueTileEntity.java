package io.github.a1qs.vaultadditions.block.blockentity;

import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import iskallia.vault.block.entity.SoulPlaqueTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RaidPlaqueTileEntity extends SoulPlaqueTileEntity {
    public RaidPlaqueTileEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RAID_PLAQUE_BLOCK_ENTITY.get(), pos, state);
    }
}
