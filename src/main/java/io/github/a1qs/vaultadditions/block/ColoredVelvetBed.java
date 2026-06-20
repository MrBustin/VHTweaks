package io.github.a1qs.vaultadditions.block;

import io.github.a1qs.vaultadditions.block.blockentity.ColoredVelvetBedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ColoredVelvetBed extends BedBlock {
    public ColoredVelvetBed(DyeColor pColor, BlockBehaviour.Properties pProperties) {
        super(pColor, pProperties);
    }

    @Override
    public void fallOn(@NotNull Level pLevel, @NotNull BlockState pBlockState, @NotNull BlockPos pBlockPos, Entity pEntity, float pFallDistance) {
        pEntity.causeFallDamage(pFallDistance, 0.0F, DamageSource.FALL);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new ColoredVelvetBedBlockEntity(pPos, pState, this.getColor());
    }
}
