package io.github.a1qs.vaultadditions.block;

import io.github.a1qs.vaultadditions.block.blockentity.RaidPlaqueTileEntity;
import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import io.github.a1qs.vaultadditions.item.RaidPlaqueBlockItem;
import iskallia.vault.block.SoulPlaqueBlock;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RaidPlaqueBlock extends SoulPlaqueBlock {
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.RAID_PLAQUE_BLOCK_ENTITY.get().create(pPos, pState);
    }

    @Override
    public void setPlacedBy(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (!world.isClientSide) {
            int tier = RaidPlaqueBlockItem.getTier(stack).orElseGet(() -> {
                int score = RaidPlaqueBlockItem.getScore(stack);
                return Configs.RAID_PLAQUE_CONFIG.getTier(score);
            });
            world.setBlock(pos, state.setValue(TIER, tier), 2);
        }
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockHelper.getTicker(pBlockEntityType, ModBlockEntities.RAID_PLAQUE_BLOCK_ENTITY.get(), (level, pos, state, tile) -> {
            int tier = Configs.RAID_PLAQUE_CONFIG.getTier(tile.getScore());
            if (state.getValue(TIER) != tier) {
                level.setBlock(pos, state.setValue(TIER, tier), 2);
            }
        });
    }

    @Override
    public void playerWillDestroy(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!world.isClientSide && !player.isCreative()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            ItemStack stack;
            if (tileEntity instanceof RaidPlaqueTileEntity plaque) {
                stack = RaidPlaqueBlockItem.create(plaque.getUuid(), plaque.getSkin().getLatestNickname(), plaque.getScore());
            } else {
                stack = new ItemStack(this);
            }
            ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, stack);
            itemEntity.setDefaultPickUpDelay();
            world.addFreshEntity(itemEntity);
        }

        this.spawnDestroyParticles(world, player, pos, state);
        if (state.is(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinAi.angerNearbyPiglins(player, false);
        }

        world.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (ModConfigs.isInitialized()) {
            for (int score : Configs.RAID_PLAQUE_CONFIG.getScoreToTier().keySet()) {
                items.add(RaidPlaqueBlockItem.create(null, null, score));
            }
        }
    }
}
