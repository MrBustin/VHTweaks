package io.github.a1qs.vaultadditions.util;

import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.config.vault.AbstractStatueLootConfig;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class StatueLootSanitizer {
    private StatueLootSanitizer() {}

    public static ItemStack sanitizeCustomStatueLoot(BlockState state, ItemStack stack) {
        return sanitize(stack, getConfig(state));
    }

    public static ItemStack sanitizeOmegaLoot(ItemStack stack) {
        return sanitize(stack, Configs.STATUE_LOOT_OMEGA);
    }

    private static ItemStack sanitize(ItemStack stack, AbstractStatueLootConfig config) {
        if (stack.isEmpty() || config == null) {
            return stack.copy();
        }

        ItemStack fallback = ItemStack.EMPTY;
        WeightedList<ProductEntry> drops = config.getDrops();
        for (int i = 0; i < drops.size(); i++) {
            ItemStack configured = drops.get(i).value.generateItemStack();
            if (configured.isEmpty() || configured.getItem() != stack.getItem()) {
                continue;
            }

            if (fallback.isEmpty()) {
                fallback = configured.copy();
            }

            if (ItemStack.isSame(configured, stack) && ItemStack.tagMatches(configured, stack)) {
                return configured.copy();
            }
        }

        if (!fallback.isEmpty()) {
            return fallback;
        }

        ItemStack sanitized = stack.copy();
        sanitized.setTag(null);
        return sanitized;
    }

    private static AbstractStatueLootConfig getConfig(BlockState state) {
        return switch (state.getBlock().getRegistryName().toString()) {
            case "vaultadditions:loot_statue_gift" -> Configs.STATUE_LOOT_GIFT;
            case "vaultadditions:loot_statue_gift_mega" -> Configs.STATUE_LOOT_MEGA_GIFT;
            case "vaultadditions:loot_statue_arena" -> Configs.STATUE_LOOT_ARENA;
            default -> Configs.STATUE_LOOT_VAULT;
        };
    }
}
