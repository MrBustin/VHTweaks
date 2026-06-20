package xyz.iwolfking.woldsvaults.blocks;

import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import xyz.iwolfking.woldsvaults.blocks.tiles.DecoBossRunePillarEntity;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

import java.util.stream.Stream;

public class DecoBossRunePillarBlock extends Block implements EntityBlock {
    private static VoxelShape SHAPE = (VoxelShape) Stream.of(Block.box(5.5, 7.0, 11.5, 10.5, 10.0, 12.5), Block.box(2.0, 12.0, 2.0, 14.0, 14.0, 14.0), Block.box(3.0, 10.0, 3.0, 13.0, 12.0, 13.0), Block.box(3.0, 0.0, 3.0, 13.0, 2.0, 13.0), Block.box(6.0, 2.0, 6.0, 10.0, 6.0, 10.0), Block.box(5.0, 6.0, 4.5, 11.0, 10.0, 11.5), Block.box(4.0, 7.0, 5.0, 12.0, 10.0, 11.0), Block.box(5.5, 7.0, 3.5, 10.5, 10.0, 4.5)).reduce((v1, v2) -> {
        return Shapes.join(v1, v2, BooleanOp.OR);
    }).get();

    public DecoBossRunePillarBlock() {
        super(Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE).strength(2.0F, 3600000.0F).noOcclusion().lightLevel((state) -> {
            return 12;
        }));
        this.registerDefaultState((BlockState)this.stateDefinition.any());
    }

    public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level level, BlockState state, BlockEntityType<A> blockEntityType) {
        return level.isClientSide() ? BlockHelper.getTicker(blockEntityType, ModBlocks.DECO_BOSS_RUNE_PILLAR_ENTITY_BLOCK_ENTITY_TYPE, DecoBossRunePillarEntity::tickClient) : null;
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlocks.DECO_GRID_GATEWAY_TILE_ENTITY_BLOCK_ENTITY_TYPE.create(pPos, pState);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }
}
