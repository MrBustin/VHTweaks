package xyz.iwolfking.woldsvaults.blocks;

import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.blocks.tiles.DecoMonolithTileEntity;
import xyz.iwolfking.woldsvaults.init.ModBlocks;


import java.util.stream.Stream;

public class DecoMonolithBlock extends Block implements EntityBlock {
    public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);
    private static final VoxelShape SHAPE = (VoxelShape) Stream.of(Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.box(2.0, 8.0, 2.0, 14.0, 10.0, 14.0)).reduce((v1, v2) -> {
        return Shapes.join(v1, v2, BooleanOp.OR);
    }).get();


    public DecoMonolithBlock() {
        super(Properties.of(Material.STONE).sound(SoundType.METAL).strength(2.0F, 3600000.0F).noOcclusion().lightLevel((state) -> {
            return state.getValue(STATE) == State.LIT ? 15 : 0;
        }));
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(STATE, State.EXTINGUISHED));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{STATE});
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlocks.DECO_MONOLITH_TILE_ENTITY_BLOCK_ENTITY_TYPE.create(pos, state);
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return BlockHelper.getTicker(type, ModBlocks.DECO_MONOLITH_TILE_ENTITY_BLOCK_ENTITY_TYPE, DecoMonolithTileEntity::tick);
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (state.getValue(DecoMonolithBlock.STATE) == State.EXTINGUISHED) {
                world.setBlock(pos, (BlockState) world.getBlockState(pos).setValue(DecoMonolithBlock.STATE, State.LIT), 3);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(DecoMonolithBlock.STATE) == State.LIT && player.isShiftKeyDown()) {
                world.setBlock(pos, (BlockState) world.getBlockState(pos).setValue(DecoMonolithBlock.STATE, State.DESTROYED), 3);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(DecoMonolithBlock.STATE) == State.DESTROYED) {
                world.setBlock(pos, (BlockState) world.getBlockState(pos).setValue(DecoMonolithBlock.STATE, State.EXTINGUISHED), 3);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(DecoMonolithBlock.STATE) == State.LIT) {
                world.setBlock(pos, (BlockState) world.getBlockState(pos).setValue(DecoMonolithBlock.STATE, State.EXTINGUISHED), 3);
                world.playSound((Player)null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        return InteractionResult.PASS;


    }

    public static enum State implements StringRepresentable {
        EXTINGUISHED,
        LIT,
        DESTROYED;

        private State() {
        }

        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
