package xyz.iwolfking.woldsvaults.blocks.tiles.challenge;

import iskallia.vault.core.vault.challenge.base.ChallengeManager;
import iskallia.vault.block.entity.challenge.ChallengeControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import xyz.iwolfking.woldsvaults.init.ModBlocks;

public class ProtectVillagerControllerTileEntity extends ChallengeControllerBlockEntity<ChallengeManager> {
    public ProtectVillagerControllerTileEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.PROTECT_VILLAGER_CONTROLLER_TILE_ENTITY, pos, state);
    }

    @Override
    public ChallengeManager createManager() {
        return null;
    }
}
