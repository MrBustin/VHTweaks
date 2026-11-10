package xyz.iwolfking.woldsvaults.blocks.challenge;

import xyz.iwolfking.woldsvaults.blocks.tiles.challenge.TempleControllerProxyBlockEntity;
import xyz.iwolfking.woldsvaults.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
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

public class TempleControllerProxyBlock extends Block implements EntityBlock {

    public TempleControllerProxyBlock() {
        super(Properties.of(Material.GLASS)
                .sound(SoundType.STONE)
                .strength(-1F, 3600000F)
                .lightLevel(value -> 10)
                .noCollission()
                .noOcclusion()
                .noDrops());

        this.registerDefaultState(this.stateDefinition.any());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TempleControllerProxyBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return BlockHelper.getTicker(type, ModBlocks.TEMPLE_CONTROLLER_PROXY_TILE_ENTITY, TempleControllerProxyBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
