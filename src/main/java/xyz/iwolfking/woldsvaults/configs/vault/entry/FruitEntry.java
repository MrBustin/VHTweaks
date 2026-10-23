package xyz.iwolfking.woldsvaults.configs.vault.entry;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import iskallia.vault.item.ItemVaultFruit;
import xyz.iwolfking.woldsvaults.mixin.the_vault.fruit_cake.AccessorItemVaultFruit;

public class FruitEntry {
    @Expose
    @SerializedName("id")
    public final String id;
    @Expose
    @SerializedName("display_name")
    public final String displayName;
    @Expose
    @SerializedName("ticks")
    public final int ticks;
    @Expose
    @SerializedName("weight")
    public final float weight;

    public FruitEntry(String id, String displayName, int ticks, float weight) {
        this.id = id;
        this.displayName = displayName;
        this.ticks = ticks;
        this.weight = weight;
    }

    public FruitEntry(ItemVaultFruit fruit, float weight) {
        this(fruit.getRegistryName().toString(), fruit.getName(fruit.getDefaultInstance()).getString(), ((AccessorItemVaultFruit) fruit).getExtraVaultTicks(), weight);
    }

    public float getWeight() {
        return weight;
    }
}