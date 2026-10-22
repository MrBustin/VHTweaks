//
// Created by BONNe
// Copyright - 2024
//


package xyz.iwolfking.woldsvaults.configs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.VaultMod;
import iskallia.vault.config.Config;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.configs.vault.entry.FruitEntry;
import xyz.iwolfking.woldsvaults.configs.vault.entry.ModifierCounter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FruitCakeConfig extends Config {
    @Expose
    @SerializedName("chance")
    private float chance = 0.1f;
    @Expose
    @SerializedName("start_modifiers")
    private Map<ResourceLocation, Integer> startModifiers = new HashMap<>();
    @Expose
    @SerializedName("fruits")
    private Map<String, FruitEntry> fruits = new HashMap<>();
    @Expose
    @SerializedName("fruit_cake_format")
    private String fruitCakeFormat = "%fruit% Cake";
    @Expose
    @SerializedName("time_added_format")
    private String timeAddedFormat = "%seconds% seconds to the Vault Timer";
    @Expose
    @SerializedName("replace_slice_with_time_added")
    private boolean replaceSliceWithTimeAdded = true;

    private List<ModifierCounter> modifiers = new ArrayList<>();

    private final Map<Integer, TreeMap<Float, FruitEntry>> fruitChances = new ConcurrentHashMap<>();

    @Override
    protected void onLoad(@Nullable Config config) {
        if (chance <= 0) {
            WoldsVaults.LOGGER.info("[FruitCakeConfig] Chance is <= 0, fruit cake vaults will not spawn.");
        }

        for (Map.Entry<ResourceLocation, Integer> entry : startModifiers.entrySet()) {
            ResourceLocation id = entry.getKey();
            if (VaultModifierRegistry.get(id) == null) {
                WoldsVaults.LOGGER.warn("[FruitCakeConfig] Invalid start modifier id '{}', skipping", id);
                continue;
            }
            this.modifiers.add(new ModifierCounter(id, entry.getValue()));
        }
        this.modifiers = List.copyOf(this.modifiers);
    }

    @Override
    public void reset() {
        this.chance = 0.1f;

        this.startModifiers = new HashMap<>();
        this.startModifiers.put(VaultMod.id("rotten"), 1);
        this.startModifiers.put(VaultMod.id("shortened"), 15);

        this.fruits = new HashMap<>();
        FruitEntry kiwi = new FruitEntry(ModItems.SWEET_KIWI, 0.6888f);
        this.fruits.put(kiwi.id, kiwi);
        FruitEntry grapes = new FruitEntry(ModItems.GRAPES, 0.1721f);
        this.fruits.put(grapes.id, grapes);
        FruitEntry lemon = new FruitEntry(ModItems.BITTER_LEMON, 0.0767f);
        this.fruits.put(lemon.id, lemon);
        FruitEntry mango = new FruitEntry(ModItems.MANGO, 0.0340f);
        this.fruits.put(mango.id, mango);
        FruitEntry orange = new FruitEntry(ModItems.SOUR_ORANGE, 0.0191f);
        this.fruits.put(orange.id, orange);
        FruitEntry star = new FruitEntry(ModItems.STAR_FRUIT, 0.0085f);
        this.fruits.put(star.id, star);
        FruitEntry pear = new FruitEntry(ModItems.MYSTIC_PEAR, 0.0008f);
        this.fruits.put(pear.id, pear);
    }

    public TreeMap<Float, FruitEntry> getFruitChances() {
        return this.fruitChances.computeIfAbsent(-1, dummy -> {
            TreeMap<Float, FruitEntry> fruitChances = new TreeMap<>();
            float totalChance = (float) this.fruits.values().stream().mapToDouble(FruitEntry::getWeight).sum();
            float fruitChance = 0;
            for (FruitEntry fruit : this.fruits.values()) {
                fruitChance += (fruit.weight / totalChance);
                fruitChances.put(fruitChance, fruit);
            }
            return fruitChances;
        });
    }

    public float getChance() {
        return this.chance;
    }

    public List<ModifierCounter> getModifiers() {
        return this.modifiers;
    }

    public FruitEntry getFruit(String id) {
        return this.fruits.get(id);
    }

    public String getFruitCakeFormat() {
        return this.fruitCakeFormat;
    }

    public String getTimeAddedFormat() {
        return this.timeAddedFormat;
    }

    public boolean isReplaceSliceWithTimeAdded() {
        return this.replaceSliceWithTimeAdded;
    }

    @Override
    public String getName() {
        return "fruit_cake";
    }
}


