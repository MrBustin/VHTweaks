package xyz.iwolfking.woldsvaults.blocks;

import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.blocks.tiles.DecoGridGatewayTileEntity;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

import javax.annotation.Nonnull;


public class DecoGridGatewayBlock extends Block implements EntityBlock {
    public DecoGridGatewayBlock() {
        super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0F, 3600000.0F).noOcclusion());
        this.registerDefaultState((BlockState)this.stateDefinition.any());
    }

    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return Block.box(3.0, 0.0, 3.0, 13.0, 18.0, 13.0);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Only register client-side ticker for particles
        return level.isClientSide() ?
                BlockHelper.getTicker(type, ModBlocks.DECO_GRID_GATEWAY_TILE_ENTITY_BLOCK_ENTITY_TYPE, DecoGridGatewayTileEntity::clientTick):
                null;
    }

    @Nonnull
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DecoGridGatewayTileEntity(pPos, pState);
    }

    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return (Integer)level.getBlockEntity(pos, ModBlocks.DECO_GRID_GATEWAY_TILE_ENTITY_BLOCK_ENTITY_TYPE)
                .map(DecoGridGatewayTileEntity::getLight).orElse(0);
    }
}
