package io.github.a1qs.vaultadditions.block.blockentity;

import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.init.ModBlockEntities;
import io.github.a1qs.vaultadditions.util.StatueLootSanitizer;
import iskallia.vault.block.entity.SkinnableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class LootStatueBlockEntity extends SkinnableTileEntity {
    private int currentTick = 0;
    private int totalItems;
    private int itemsRemaining;
    private ItemStack lootItem;

    public LootStatueBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LOOT_STATUE_BLOCK_ENTITY.get(), pos, state);
        this.lootItem = ItemStack.EMPTY;
        switch(state.getBlock().getRegistryName().toString()) {
            case "vaultadditions:loot_statue_gift" -> this.itemsRemaining = Configs.STATUE_LOOT_GIFT.getRandomItemCount();
            case "vaultadditions:loot_statue_gift_mega" -> this.itemsRemaining = Configs.STATUE_LOOT_MEGA_GIFT.getRandomItemCount();
            case "vaultadditions:loot_statue_arena" -> this.itemsRemaining = Configs.STATUE_LOOT_ARENA.getRandomItemCount();
            default -> this.itemsRemaining = Configs.STATUE_LOOT_VAULT.getRandomItemCount();
        }

        this.totalItems = itemsRemaining;
    }

    public int getCurrentTick() {
        return this.currentTick;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getItemsRemaining() {
        return itemsRemaining;
    }

    public ItemStack getLootItem() {
        return this.lootItem;
    }

    public boolean isDead() {
        return itemsRemaining <= 0;
    }

    public void setLootItem(@Nonnull ItemStack stack) {
        this.lootItem = StatueLootSanitizer.sanitizeCustomStatueLoot(this.getBlockState(), stack);
        this.setChanged();
        this.sendUpdates();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LootStatueBlockEntity tile) {
        if (!level.isClientSide) {
            if(!tile.isDead()) {
                if (tile.currentTick++ >= tile.getModifiedInterval(state)) {
                    tile.currentTick = 0;
                    if (!tile.lootItem.isEmpty()) {
                        ItemStack stack = tile.lootItem.copy();
                        if (tile.dispenseItem(stack, false)) {
                            tile.itemsRemaining--;
                            tile.setChanged();

                        }
                    }
                }
            }
        }
    }

    private int getModifiedInterval(BlockState state) {
        switch(state.getBlock().getRegistryName().toString()) {
            case "vaultadditions:loot_statue_gift" -> {
                return Configs.STATUE_LOOT_GIFT.getInterval();
            }
            case "vaultadditions:loot_statue_gift_mega" -> {
                return Configs.STATUE_LOOT_MEGA_GIFT.getInterval();
            }
            case "vaultadditions:loot_statue_arena" -> {
                return Configs.STATUE_LOOT_ARENA.getInterval();
            }
            default -> {
                return Configs.STATUE_LOOT_VAULT.getInterval();
            }
        }
    }

    private boolean dispenseItem(ItemStack stack, boolean simulate) {
        assert this.level != null;

        BlockPos down = this.getBlockPos().below();
        BlockEntity tileEntity = this.level.getBlockEntity(down);
        if (tileEntity != null) {
            LazyOptional<IItemHandler> handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
            handler.ifPresent((h) -> {
                ItemHandlerHelper.insertItemStacked(h, stack, simulate);
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void updateSkin() {}

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        String nickname = this.skin.getLatestNickname();
        if (nickname != null) {
            nbt.putString("PlayerNickname", nickname);
        }

        nbt.putInt("TotalItems", this.getTotalItems());
        nbt.putInt("ItemsRemaining", this.getItemsRemaining());
        nbt.putInt("CurrentTick", this.getCurrentTick());

        nbt.put("LootItem", this.getLootItem().serializeNBT());
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("PlayerNickname")) {
            this.skin.updateSkin(nbt.getString("PlayerNickname"));
        }

        this.totalItems = nbt.getInt("TotalItems");
        this.itemsRemaining = nbt.getInt("ItemsRemaining");
        this.currentTick = nbt.getInt("CurrentTick");
        this.lootItem = StatueLootSanitizer.sanitizeCustomStatueLoot(this.getBlockState(), ItemStack.of(nbt.getCompound("LootItem")));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }
}
