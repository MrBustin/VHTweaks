package xyz.iwolfking.woldsvaults.configs.vault.entry;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import iskallia.vault.item.ItemVaultFruit;
import net.minecraft.world.item.Item;
import xyz.iwolfking.woldsvaults.mixin.the_vault.fruit_cake.AccessorItemVaultFruit;

public class FruitEntry {
    @Expose
    public final String id;
    @Expose
    public final String displayName;
    @Expose
    public final String icon;
    @Expose
    public final int ticks;
    @Expose
    public final float weight;

    public final Item iconItem;

    public FruitEntry(String id, String displayName, Item iconItem, int ticks, float weight) {
        this.id = id;
        this.displayName = displayName;
        this.icon = iconItem.getRegistryName().toString();
        this.iconItem = iconItem;
        this.ticks = ticks;
        this.weight = weight;
    }

    public FruitEntry(ItemVaultFruit fruit, float weight) {
        this(fruit.getRegistryName().toString(), fruit.getName(fruit.getDefaultInstance()).getString(), fruit, ((AccessorItemVaultFruit) fruit).getExtraVaultTicks(), weight);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (!iconItem.getRegistryName().toString().equals(id)) {
            json.addProperty("icon", iconItem.getRegistryName().toString());
        }
        if (!(iconItem instanceof AccessorItemVaultFruit fruit) || fruit.getExtraVaultTicks() != ticks) {
            json.addProperty("ticks", ticks);
        }
        json.addProperty("weight", weight);
        return json;
    }

    public float getWeight() {
        return weight;
    }
}