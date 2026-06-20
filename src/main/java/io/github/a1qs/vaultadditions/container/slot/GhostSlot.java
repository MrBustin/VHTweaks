package io.github.a1qs.vaultadditions.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GhostSlot extends Slot {
    public GhostSlot(Container pContainer, int pIndex, int pX, int pY) {
        super(pContainer, pIndex, pX, pY);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return true;
    }

    @Override
    public void set(ItemStack stack) {
        // Set the filter item as a single item for display
        if (!stack.isEmpty()) {
            ItemStack copy = stack.copy();
            copy.setCount(1); // Ensure only one item is stored
            super.set(copy);
        } else {
            super.set(ItemStack.EMPTY); // Clear the slot
        }
    }

    @Override
    public void onTake(@NotNull Player pPlayer, ItemStack pStack) {
        pStack.setCount(0);
        super.onTake(pPlayer, pStack);
    }

    @Override
    public int getMaxStackSize() {
        // Limit the stack size to 1 for filtering
        return 1;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        // Limit the stack size to 1 for filtering
        return 1;
    }


}

