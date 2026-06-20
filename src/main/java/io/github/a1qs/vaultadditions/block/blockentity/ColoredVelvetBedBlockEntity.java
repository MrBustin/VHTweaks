package io.github.a1qs.vaultadditions.block.blockentity;

import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredVelvetBedBlockEntity extends BlockEntity {
    private DyeColor color;

    public ColoredVelvetBedBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.COLORED_VELVET_BED_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        this.color = ((BedBlock)pBlockState.getBlock()).getColor();
    }

    public ColoredVelvetBedBlockEntity(BlockPos pWorldPosition, BlockState pBlockState, DyeColor pColor) {
        super(ModBlockEntities.COLORED_VELVET_BED_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        this.color = pColor;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor pColor) {
        this.color = pColor;
    }
}
