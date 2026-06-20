package io.github.a1qs.vaultadditions.block;

import iskallia.vault.block.base.FacedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class RotateableFoolsGold extends FacedBlock {
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 1.25, 16.0, 6.25, 14.0);

    public RotateableFoolsGold(Properties properties) {
        super(properties);
    }

    @Override @ParametersAreNonnullByDefault
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
}
