package xyz.iwolfking.woldsvaults.configs.vault.entry;

import com.google.gson.JsonObject;
import iskallia.vault.item.ItemVaultFruit;
import net.minecraft.world.item.Item;
import xyz.iwolfking.woldsvaults.mixin.the_vault.fruit_cake.AccessorItemVaultFruit;

public record Fruit(String id, String displayName, Item icon, int ticks, float weight) {
    public Fruit(ItemVaultFruit icon, float weight) {
        this(icon.getRegistryName().toString(), icon.getName(icon.getDefaultInstance()).getString(), icon, ((AccessorItemVaultFruit) icon).getExtraVaultTicks(), weight);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (!icon.getRegistryName().toString().equals(id)) {
            json.addProperty("icon", icon.getRegistryName().toString());
        }
        if (!(icon instanceof AccessorItemVaultFruit fruit) || fruit.getExtraVaultTicks() != ticks) {
            json.addProperty("ticks", ticks);
        }
        json.addProperty("weight", weight);
        return json;
    }
}