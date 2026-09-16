package xyz.iwolfking.woldsvaults.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import xyz.iwolfking.woldsvaults.entities.CrystalVendorEntity;
import xyz.iwolfking.woldsvaults.init.ModEntities;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class CrystalTraderControllerBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public CrystalTraderControllerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        Direction traderFacing = placer == null ? state.getValue(FACING).getOpposite() : placer.getDirection().getOpposite();
        spawnTrader(serverLevel, pos, traderFacing);
        serverLevel.removeBlock(pos, false);
    }

    private void spawnTrader(ServerLevel serverLevel, BlockPos pos, Direction facing) {
        CrystalVendorEntity trader = ModEntities.CRYSTAL_VENDOR_ENTITY.create(serverLevel);
        if (trader == null) {
            return;
        }

        float yaw = facing.toYRot();
        trader.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos), MobSpawnType.SPAWNER, null, null);
        trader.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, yaw, 0.0F);
        trader.setYRot(yaw);
        trader.setYBodyRot(yaw);
        trader.setYHeadRot(yaw);
        serverLevel.addFreshEntity(trader);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
