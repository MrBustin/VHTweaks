package xyz.iwolfking.woldsvaults.util;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class CrystalTraderCurrencyHelper {
    public static ItemStack createCurrencyStack() {
        return new ItemStack(ModBlocks.VAULT_GOLD);
    }

    public static int getCurrencyCount(Player player) {
        int count = 0;

        for (InventoryUtil.ItemAccess itemAccess : InventoryUtil.findAllItems(player)) {
            ItemStack stack = itemAccess.getStack();
            if (isCurrency(stack)) {
                count += stack.getCount();
            }
        }

        return count;
    }

    public static boolean consumeCurrency(Player player, int amount) {
        if (amount <= 0 || player.isCreative()) {
            return true;
        }

        if (getCurrencyCount(player) < amount) {
            return false;
        }

        int remaining = amount;
        for (InventoryUtil.ItemAccess itemAccess : InventoryUtil.findAllItems(player)) {
            ItemStack stack = itemAccess.getStack();
            if (!isCurrency(stack)) {
                continue;
            }

            int consumed = Math.min(remaining, stack.getCount());
            itemAccess.setStack(ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - consumed));
            remaining -= consumed;

            if (remaining <= 0) {
                return true;
            }
        }

        return false;
    }

    private static boolean isCurrency(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ModBlocks.VAULT_GOLD;
    }
}
