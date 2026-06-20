package io.github.a1qs.vaultadditions.block.blockentity;

import io.github.a1qs.vaultadditions.block.EventConditionBlock;
import io.github.a1qs.vaultadditions.data.EventData;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EventConditionBlockEntity extends BlockEntity {
    private int tickCounter = 0;

    public EventConditionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EVENT_CONDITION_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tickEntity(Level pLevel, BlockPos pPos, BlockState pBlockState, EventConditionBlockEntity pBlockEntity) {
        if (!pLevel.isClientSide()) {
            if(pBlockState.getBlock() instanceof EventConditionBlock conditionBlock) {
                if (pBlockEntity.tickCounter == 0) {
                    conditionBlock.updatePowerState(pLevel, pPos, EventData.getServer().conditionsCompleted());
                    pLevel.sendBlockUpdated(pPos, pBlockState, pBlockState, Block.UPDATE_ALL);
                }

                pBlockEntity.tickCounter = (pBlockEntity.tickCounter + 1) % 100;
            }
        }
    }
}
