package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.annotations.Expose;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.init.ModItems;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.util.data.WeightedList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExtraVaultChestMeta extends Config {
    @Expose
    private final Map<Block, Map<VaultRarity, Double>> powerCrystalChances = new LinkedHashMap<>();

    @Expose
    private final WeightedList<ItemEntry> itemsToConsider = new WeightedList<>();

    @Override
    protected void onLoad(@Nullable Config oldConfigInstance) {
        if (itemsToConsider.isEmpty()) {
            VaultAdditions.LOGGER.error("[ExtraVaultChestMeta] Items to consider should have more than 0 entries, defaulting to power crystal");
            this.itemsToConsider.add(new WeightedList.Entry<>(new ItemEntry(ModItems.POWER_CRYSTAL.get(), 3), 5));
        } else if (itemsToConsider.getTotalWeight() <= 0) {
            VaultAdditions.LOGGER.error("[ExtraVaultChestMeta] Items to consider's total weight must be greater than 0, defaulting to power crystal");
            this.itemsToConsider.add(new WeightedList.Entry<>(new ItemEntry(ModItems.POWER_CRYSTAL.get(), 3), 5));
        }
    }

    @Override
    public String getName() {
        return "vaultadditions_extra_vault_chest_meta";
    }

    @Override
    protected void reset() {
        this.powerCrystalChances.clear();
        this.itemsToConsider.clear();
        this.set(ModBlocks.WOODEN_CHEST, 0.0F);
        this.set(ModBlocks.GILDED_CHEST, 0.2F);
        this.set(ModBlocks.LIVING_CHEST, 0.0F);
        this.set(ModBlocks.ORNATE_CHEST, 0.5F);
        this.set(ModBlocks.ALTAR_CHEST, 0.7F);
        this.set(ModBlocks.TREASURE_CHEST, 0.5F);
        this.set(ModBlocks.ORNATE_STRONGBOX, 0.5F);
        this.set(ModBlocks.GILDED_STRONGBOX, 0.2F);
        this.set(ModBlocks.LIVING_STRONGBOX, 0.0F);
        this.set(ModBlocks.ORNATE_BARREL, 0.5F);
        this.set(ModBlocks.GILDED_BARREL, 0.2F);
        this.set(ModBlocks.LIVING_BARREL, 0.0F);
        this.set(ModBlocks.WOODEN_BARREL, 0.0F);
        this.itemsToConsider.add(new WeightedList.Entry<>(new ItemEntry(ModItems.POWER_CRYSTAL.get() ,3), 5));
    }

    public double getPowerCrystalChance(Block block, VaultRarity rarity) {
        return this.powerCrystalChances.getOrDefault(block, Collections.emptyMap()).getOrDefault(rarity, 0.0);
    }

    private void set(Block block, double chance) {
        for (VaultRarity rarity : VaultRarity.values()) {
            this.powerCrystalChances.computeIfAbsent(block, (b) -> new LinkedHashMap<>()).put(rarity, chance);
        }
    }

    public ItemStack randomItem() {
        ItemEntry entry = this.itemsToConsider.getRandom(rand);
        return entry == null ? ItemStack.EMPTY : entry.createItemStack();
    }
}
