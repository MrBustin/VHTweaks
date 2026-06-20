package xyz.iwolfking.woldsvaults.init;

import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.template.DynamicTemplate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ModObjectiveTemplates {
    public static final DynamicTemplate ALCHEMY_OBJECTIVE_TEMPLATE = createDynamicTemplate(new BlockPos(0, 0, 0), ModBlocks.BREWING_ALTAR.defaultBlockState());

    private static DynamicTemplate createDynamicTemplate(BlockPos pos, BlockState state) {
        DynamicTemplate template = new DynamicTemplate();
        template.add(PartialTile.of(PartialBlockState.of(state), PartialCompoundNbt.empty(), pos));
        return template;
    }
}
