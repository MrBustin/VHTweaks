package xyz.iwolfking.woldsvaults.blocks.challenge;

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
import xyz.iwolfking.woldsvaults.blocks.tiles.challenge.ProtectVillagerControllerProxyBlockEntity;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

public class ProtectVillagerControllerProxyBlock extends Block implements EntityBlock {
    public ProtectVillagerControllerProxyBlock() {
        super(Properties.of(Material.GLASS).sound(SoundType.STONE).strength(-1.0F, 3600000.0F).lightLevel((value) -> 10).noCollission().noOcclusion().noDrops());
        this.registerDefaultState((BlockState)this.stateDefinition.any());
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ProtectVillagerControllerProxyBlockEntity(pos, state);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockHelper.getTicker(pBlockEntityType, ModBlocks.PROTECT_VILLAGER_CONTROLLER_PROXY_TILE_ENTITY, ProtectVillagerControllerProxyBlockEntity::tick);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
