package xyz.iwolfking.woldsvaults.blocks;

import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.blocks.tiles.DecoObeliskTileEntity;
import xyz.iwolfking.woldsvaults.init.ModBlocks;


public class DecoObeliskBlock extends Block implements EntityBlock {

    public DecoObeliskBlock() {
        super(Properties.of(Material.STONE).sound(SoundType.METAL).strength(2.0F, 3600000.0F));
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER)).setValue(FILLED, false));
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? ModBlocks.DECO_OBELISK_TILE_ENTITY_BLOCK_ENTITY_TYPE.create(pos, state) : null;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return BlockHelper.getTicker(type, ModBlocks.DECO_OBELISK_TILE_ENTITY_BLOCK_ENTITY_TYPE, DecoObeliskTileEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    public static final EnumProperty<DoubleBlockHalf> HALF;
    public static final BooleanProperty FILLED;
    private static final VoxelShape SHAPE;
    private static final VoxelShape SHAPE_TOP;


    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HALF}).add(new Property[]{FILLED});
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_TOP : SHAPE;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    private void spawnParticles(Level world, BlockPos pos) {
        for(int i = 0; i < 20; ++i) {
            double d0 = world.random.nextGaussian() * 0.02;
            double d1 = world.random.nextGaussian() * 0.02;
            double d2 = world.random.nextGaussian() * 0.02;
            ((ServerLevel)world).sendParticles(ParticleTypes.POOF, (double)pos.getX() + world.random.nextDouble() - d0, (double)pos.getY() + world.random.nextDouble() - d1, (double)pos.getZ() + world.random.nextDouble() - d2, 10, d0, d1, d2, 1.0);
        }

        world.playSound((Player)null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, world, pos, newState, isMoving);
        if (!state.is(newState.getBlock())) {
            BlockState otherState;
            if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                otherState = world.getBlockState(pos.below());
                if (otherState.is(state.getBlock())) {
                    world.removeBlock(pos.below(), isMoving);
                }
            } else {
                otherState = world.getBlockState(pos.above());
                if (otherState.is(state.getBlock())) {
                    world.removeBlock(pos.above(), isMoving);
                }
            }
        }

    }

    static {
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
        FILLED = BooleanProperty.create("filled");
        SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 32.0, 14.0);
        SHAPE_TOP = SHAPE.move(0.0, -1.0, 0.0);
    }

}
