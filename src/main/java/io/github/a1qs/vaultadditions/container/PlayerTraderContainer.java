package io.github.a1qs.vaultadditions.container;

import io.github.a1qs.vaultadditions.block.blockentity.PlayerTraderBlockEntity;
import io.github.a1qs.vaultadditions.init.ModContainers;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.slot.TabSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PlayerTraderContainer extends OverSizedSlotContainer {
    private SlotIndexRange playerInventoryIndexRange;
    private SlotIndexRange hotbarIndexRange;
    private final PlayerTraderBlockEntity blockEntity;
    private final BlockPos pos;


    public PlayerTraderContainer(int pId, Level pLevel, BlockPos pPos, Inventory pPlayerInventory) {
        super(ModContainers.PLAYER_TRADER_CONTAINER, pId, pPlayerInventory.player);

        this.pos = pPos;

        if (pLevel.getBlockEntity(this.pos) instanceof PlayerTraderBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
            this.initInventory();
        } else {
            this.blockEntity = null;
        }
    }

    private void initInventory() {
        int offsetX = 0;
        int offsetY = 15;

        int containerSlotIndex = 0;

        // init inventory slots
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                int index = column + (row + 1) * 9;
                int x = 8 + column * 18 + offsetX;
                int y = 84 + row * 18 + offsetY;
                this.addSlot(new TabSlot(player.getInventory(), index, x, y));
                containerSlotIndex++;
            }
        }
        this.playerInventoryIndexRange = new SlotIndexRange(0, containerSlotIndex);

        // init hotbar slots
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            int x = 8 + hotbarSlot * 18 + offsetX;
            int y = 142 + offsetY;
            this.addSlot(new TabSlot(player.getInventory(), hotbarSlot, x, y));
            containerSlotIndex++;
        }
        this.hotbarIndexRange = new SlotIndexRange(this.playerInventoryIndexRange.end(), containerSlotIndex);
    }

    public BlockPos getPos() {
        return pos;
    }

    public PlayerTraderBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
