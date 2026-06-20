package xyz.iwolfking.woldsvaults.blocks;

import iskallia.vault.block.entity.ScavengerAltarTileEntity;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.blocks.tiles.DecoScavengerAltarEntity;
import xyz.iwolfking.woldsvaults.init.ModBlocks;


import java.util.stream.Stream;

public class DecoScavengerAltarBlock extends Block implements EntityBlock {

    private static VoxelShape SHAPE = (VoxelShape) Stream.of(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(4.0, 3.0, 4.0, 12.0, 11.0, 12.0), Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0), Block.box(1.0, 11.0, 1.0, 15.0, 13.0, 15.0), Block.box(2.0, 9.0, 2.0, 14.0, 11.0, 14.0), Block.box(2.0, 2.0, 2.0, 14.0, 4.0, 14.0)).reduce((v1, v2) -> {
        return Shapes.join(v1, v2, BooleanOp.OR);
    }).get();

    public DecoScavengerAltarBlock() {
        super(Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GREEN).strength(2.0F, 3600000.0F).noOcclusion().lightLevel((state) -> {
            return 12;
        }));
        this.registerDefaultState((BlockState)this.stateDefinition.any());
    }


    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.CONSUME;
        } else {
            BlockEntity var8 = world.getBlockEntity(pos);
            if (var8 instanceof ScavengerAltarTileEntity) {
                ScavengerAltarTileEntity tile = (ScavengerAltarTileEntity)var8;
                boolean mainHandEmpty = player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
                boolean offHandEmpty = player.getItemInHand(InteractionHand.OFF_HAND).isEmpty();
                ItemStack existing = tile.getHeldItem().copy();
                if (offHandEmpty && mainHandEmpty && existing.isEmpty()) {
                    return InteractionResult.PASS;
                } else {
                    if (mainHandEmpty && !offHandEmpty) {
                        tile.setHeldItem(player.getItemInHand(InteractionHand.OFF_HAND).copy());
                        tile.setItemPlacedBy(player.getUUID());
                        player.setItemInHand(InteractionHand.OFF_HAND, existing);
                    } else {
                        tile.setHeldItem(player.getItemInHand(InteractionHand.MAIN_HAND).copy());
                        tile.setItemPlacedBy(player.getUUID());
                        player.setItemInHand(InteractionHand.MAIN_HAND, existing);
                    }

                    tile.ticksToConsume = 40;
                    tile.consuming = false;
                    world.playSound((Player)null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                    tile.setChanged();
                    world.sendBlockUpdated(pos, tile.getBlockState(), tile.getBlockState(), 3);
                    return InteractionResult.CONSUME;
                }
            } else {
                return InteractionResult.PASS;
            }
        }
    }
    @Override
    public @Nullable <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level level, BlockState state, BlockEntityType<A> blockEntityType) {
        return level.isClientSide() ? BlockHelper.getTicker(blockEntityType, ModBlocks.DECO_SCAVENGER_ALTAR_ENTITY_BLOCK_ENTITY_TYPE, DecoScavengerAltarEntity::tickClient) : null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
       return ModBlocks.DECO_SCAVENGER_ALTAR_ENTITY_BLOCK_ENTITY_TYPE.create(pPos, pState);
    }
}
