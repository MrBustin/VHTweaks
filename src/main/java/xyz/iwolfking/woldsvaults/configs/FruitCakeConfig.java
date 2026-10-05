//
// Created by BONNe
// Copyright - 2024
//


package xyz.iwolfking.woldsvaults.configs;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.Config;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemVaultFruit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.configs.vault.entry.FruitEntry;
import xyz.iwolfking.woldsvaults.configs.vault.entry.ModifierCounter;
import xyz.iwolfking.woldsvaults.mixin.the_vault.fruit_cake.AccessorItemVaultFruit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FruitCakeConfig extends Config {
    @Expose
    private float chance = 0.1f;
    @Expose
    private Map<ResourceLocation, Integer> startModifiers = new HashMap<>();
    @Expose
    private Map<String, FruitEntry> fruits = new HashMap<>();
    @Expose
    private Map<Integer, List<String>> fruitScaling = new HashMap<>();
    @Expose
    private String fruitCakeFormat = "%fruit% Cake";
    @Expose
    private String timeAddedFormat = "%seconds% seconds to the Vault Timer";
    @Expose
    private boolean replaceSliceWithTimeAdded = true;

    private List<ModifierCounter> modifiers = new ArrayList<>();

    private final Map<String, FruitEntry> fruitMap = new HashMap<>();
    private final TreeMap<Integer, List<FruitEntry>> scaling = new TreeMap<>();
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

//        for (Map.Entry<String, JsonObject> entry : fruits.entrySet()) {
//            String id = entry.getKey();
//            String displayName = id;
//            JsonObject json = entry.getValue();
//
//            if (json.get("displayName") instanceof JsonPrimitive displayNameJson && displayNameJson.isString()) {
//                displayName = displayNameJson.getAsString();
//            }
//
//            String iconId = id;
//            if (json.get("icon") instanceof JsonPrimitive serializedIcon && serializedIcon.isString()) {
//                iconId = serializedIcon.getAsString();
//            }
//            Item icon = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(iconId));
//            if (icon == null) {
//                WoldsVaults.LOGGER.warn("[FruitCakeConfig] Fruit '{}' has an invalid icon '{}', skipping", id, iconId);
//                continue;
//            }
//
//            if (!(icon instanceof ItemVaultFruit) && (!(json.get("ticks") instanceof JsonPrimitive ticksJson) || !ticksJson.isNumber())) {
//                WoldsVaults.LOGGER.warn("[FruitCakeConfig] Fruit '{}' has an invalid ticks '{}', skipping", id, json.get("ticks"));
//                continue;
//            }
//            int ticks = json.has("ticks") ? json.get("ticks").getAsInt() : ((AccessorItemVaultFruit) icon).getExtraVaultTicks();
//
//            if (!(json.get("weight") instanceof JsonPrimitive weightJson) || !weightJson.isNumber()) {
//                WoldsVaults.LOGGER.warn("[FruitCakeConfig] Fruit '{}' has an invalid weight '{}', skipping", id, json.get("weight"));
//                continue;
//            }
//            float weight = weightJson.getAsFloat();
//            if (weight <= 0) {
//                WoldsVaults.LOGGER.warn("[FruitCakeConfig] Fruit '{}' has a weight <= 0, skipping", id);
//                continue;
//            }
//
//            this.fruitMap.put(id, new Fruit(id, displayName, icon, ticks, weight));
//        }

        for (Map.Entry<Integer, List<String>> entry : fruitScaling.entrySet()) {
            int memberCount = entry.getKey();
            List<FruitEntry> fruits = new ArrayList<>();
            for (String fruitId : entry.getValue()) {
                FruitEntry fruit = this.fruits.get(fruitId);
                if (fruit == null) {
                    WoldsVaults.LOGGER.warn("[FruitCakeConfig] Scaling for '{}' players, fruit '{}' not found, skipping", memberCount, fruitId);
                    continue;
                }
                fruits.add(fruit);
            }
            this.scaling.put(memberCount, fruits);
        }
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

        this.fruitScaling = new HashMap<>();
        this.fruitScaling.put(30, List.of(star.id));
        this.fruitScaling.put(32, List.of(star.id, mango.id));
    }

    public TreeMap<Float, FruitEntry> getFruitChances(int memberCount) {
        Map.Entry<Integer, List<FruitEntry>> punishment = this.scaling.floorEntry(memberCount);
        if (punishment != null) {
            return fruitChances.computeIfAbsent(memberCount, count -> {
                TreeMap<Float, FruitEntry> withPunishment = new TreeMap<>();
                float totalChance = (float) this.fruits.values().stream()
                        .filter(fruit -> !punishment.getValue().contains(fruit))
                        .mapToDouble(FruitEntry::getWeight).sum();
                float fruitChance = 0;
                for (FruitEntry fruit : this.fruits.values()) {
                    if (!punishment.getValue().contains(fruit)) {
                        fruitChance += (fruit.getWeight() / totalChance);
                        withPunishment.put(fruitChance, fruit);
                    }
                }
                return withPunishment;
            });
        }

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


