package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.annotations.Expose;
import io.github.a1qs.vaultadditions.VaultAdditions;
import iskallia.vault.VaultMod;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.config.entry.vending.ProductEntry;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractStatueLootConfig extends Config {
    private static final WeightedList<ProductEntry> EMERGENCY_DROPS = new WeightedList<>(Map.of(
            new ProductEntry(Items.GRASS_BLOCK), 1,
            new ProductEntry(Items.STONE), 1,
            new ProductEntry(Items.DIORITE), 1,
            new ProductEntry(Items.ANDESITE), 1,
            new ProductEntry(Items.GRANITE), 1
    ));
    private static final int MAX_ATTEMPTS = 100_000;

    @Expose
    protected WeightedList<ProductEntry> DROPS = new WeightedList<>();
    @Expose
    protected int INTERVAL;
    @Expose
    protected int MIN_ITEM_GENERATED;
    @Expose
    protected int MAX_ITEM_GENERATED;

    protected IntRangeEntry rollRange;

    @Override
    protected void onLoad(@Nullable Config oldConfigInstance) {
        if (MIN_ITEM_GENERATED > MAX_ITEM_GENERATED) {
            VaultAdditions.LOGGER.warn("Min value is greater than Max value. Swapping values.");
            int temp = this.MIN_ITEM_GENERATED;
            this.MIN_ITEM_GENERATED = MAX_ITEM_GENERATED;
            this.MAX_ITEM_GENERATED = temp;
        }
        this.rollRange = new IntRangeEntry(MIN_ITEM_GENERATED, MAX_ITEM_GENERATED);

        for (WeightedList.Entry<ProductEntry> entry : this.DROPS.copy()) {
            if (entry.value.generateItemStack().isEmpty()) {
                VaultMod.LOGGER.error("[{}] Invalid drop entry '{}', was empty", id(), GSON.toJson(entry.value));
                this.DROPS.remove(entry);
            }
        }

        if (this.DROPS.size() <= 5) {
            VaultMod.LOGGER.error("[{}] Drops should have more than 5 entries, defaulting to emergency drops", id());
            this.DROPS = EMERGENCY_DROPS.copy();
        } else if (this.DROPS.getTotalWeight() <= 0) {
            VaultMod.LOGGER.error("[{}] Drops should have more than 0 weight, defaulting to emergency drops", id());
            this.DROPS = EMERGENCY_DROPS.copy();
        }
    }

    @Override
    protected abstract void reset();

    public WeightedList<ProductEntry> getDrops() {
        return DROPS.copy();
    }

    public IntRangeEntry getRollRange() {
        return rollRange;
    }

    public int getInterval() {
        return this.INTERVAL;
    }

    public int getRandomItemCount() {
        return rollRange.getRandom();
    }

    public List<ItemStack> getOptions() {
        Map<Item, ItemStack> options = new HashMap<>();
        for (int i = 0; i < MAX_ATTEMPTS && options.size() < 5; i++) {
            ProductEntry entry = this.DROPS.getRandom(rand);
            ItemStack item = entry.generateItemStack();
            if (!item.isEmpty()) {
                options.putIfAbsent(item.getItem(), item);
            }
        }
        return new ArrayList<>(options.values());
    }

    public ItemStack randomLoot() {
        return this.DROPS.getRandom(rand).generateItemStack();
    }

    private String id() {
        return getClass().getSimpleName();
    }
}
