package io.github.a1qs.vaultadditions.mixins.loot_statue;

import iskallia.vault.block.LootStatueUpperBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Mixin(LootStatueUpperBlock.class)
public abstract class MixinLootStatueUpperBlock extends Block {
    private MixinLootStatueUpperBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return List.of();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            BlockPos below = pos.below(state.getValue(LootStatueUpperBlock.HALF) == Half.BOTTOM ? 1 : 2);
            BlockState statue = level.getBlockState(below);
            statue.getBlock().playerWillDestroy(level, below, statue, player);
            return;
        }
        super.playerWillDestroy(level, pos, state, player);
    }
}
