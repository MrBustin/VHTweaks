package xyz.iwolfking.woldsvaults.blocks.challenge;

import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.util.VoxelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class TempleControllerBlock extends Block implements EntityBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final VoxelShape SHAPE_TOP = makeShape().move(0, -1, 0);
    private static final VoxelShape SHAPE_BOTTOM = makeShape();

    private final Supplier<BlockEntityType<? extends ChallengeControllerBlockEntity<?>>> entityType;

    public TempleControllerBlock(Supplier<BlockEntityType<? extends ChallengeControllerBlockEntity<?>>> entityType) {
        super(Properties.of(Material.GLASS)
                .sound(SoundType.GLASS)
                .strength(-1F, 3600000F)
                .lightLevel(value -> 10)
                .noCollission()
                .noOcclusion()
                .noDrops());
        this.entityType = entityType;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(FACING, Direction.NORTH));
    }

    private static VoxelShape makeShape() {
        VoxelShape m1 = Block.box(0, 0, 0, 16, 2, 16);
        VoxelShape m2 = Block.box(2, 2, 2, 14, 29, 14);
        return VoxelUtils.combineAll(BooleanOp.OR, m1, m2);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if(state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return SHAPE_TOP;
        }

        return SHAPE_BOTTOM;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, world, pos, newState, isMoving);
        if (!state.is(newState.getBlock())) {
            if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                BlockState otherState = world.getBlockState(pos.below());
                if (otherState.is(state.getBlock())) {
                    world.removeBlock(pos.below(), isMoving);
                }
            } else {
                BlockState otherState = world.getBlockState(pos.above());
                if (otherState.is(state.getBlock())) {
                    world.removeBlock(pos.above(), isMoving);
                }
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if(state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return this.entityType.get().create(pos, state);
        }

        return null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockHelper.getTicker(pBlockEntityType, this.entityType.get(), ChallengeControllerBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }


    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return super.rotate(state, rotation).setValue(FACING,
                rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return super.mirror(state, mirror).setValue(FACING,
                mirror.mirror(state.getValue(FACING)));
    }
}
