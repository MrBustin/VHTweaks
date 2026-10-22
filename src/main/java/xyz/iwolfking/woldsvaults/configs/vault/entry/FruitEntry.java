package xyz.iwolfking.woldsvaults.configs.vault.entry;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.item.ItemVaultFruit;
import net.minecraft.world.item.Item;
import xyz.iwolfking.woldsvaults.mixin.the_vault.fruit_cake.AccessorItemVaultFruit;

public class FruitEntry {
    @Expose
    @SerializedName("id")
    public final String id;
    @Expose
    @SerializedName("display_name")
    public final String displayName;
    @Expose
    @SerializedName("icon")
    public final String icon;
    @Expose
    @SerializedName("ticks")
    public final int ticks;
    @Expose
    @SerializedName("weight")
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