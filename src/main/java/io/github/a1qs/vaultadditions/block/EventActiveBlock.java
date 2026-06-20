package io.github.a1qs.vaultadditions.block;

import io.github.a1qs.vaultadditions.block.blockentity.EventActiveBlockEntity;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Random;

public class EventActiveBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public EventActiveBlock(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction) {
        return state.getValue(POWERED) ? 15 : 0; // Emits full power if powered
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true; // Block can emit redstone signals
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.EVENT_ACTIVE_BLOCK_ENTITY.get().create(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(new TextComponent("Emits a ").withStyle(ChatFormatting.YELLOW)
                .append(new TextComponent("Redstone Signal ").withStyle(ChatFormatting.RED))
                .append(new TextComponent("if a VaultAdditions Event is "))
                .append(new TextComponent("Currently Active").withStyle(ChatFormatting.LIGHT_PURPLE))
        );
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return !pLevel.isClientSide ? createTickerHelper(pBlockEntityType, ModBlockEntities.EVENT_ACTIVE_BLOCK_ENTITY.get(), EventActiveBlockEntity::tickEntity) : null;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom) {
        if (pState.getValue(POWERED)) {
            double d0 = (double)pPos.getX() + 0.5D + (pRandom.nextDouble() - 0.5D) * 0.2D;
            double d1 = (double)pPos.getY() + 0.7D + (pRandom.nextDouble() - 0.5D) * 0.2D;
            double d2 = (double)pPos.getZ() + 0.5D + (pRandom.nextDouble() - 0.5D) * 0.2D;
            pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }


    public void updatePowerState(Level level, BlockPos pos, boolean conditionMet) {
        BlockState currentState = level.getBlockState(pos);
        boolean isCurrentlyPowered = currentState.getValue(POWERED);

        if (conditionMet != isCurrentlyPowered) {
            level.setBlock(pos, currentState.setValue(POWERED, conditionMet), 3);
            level.updateNeighborsAt(pos, this);
        }
    }
}
