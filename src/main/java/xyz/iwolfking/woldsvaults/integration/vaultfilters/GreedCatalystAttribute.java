package xyz.iwolfking.woldsvaults.integration.vaultfilters;

import iskallia.vault.item.InfusedCatalystItem;
import net.joseph.vaultfilters.attributes.abstracts.BooleanAttribute;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class GreedCatalystAttribute extends BooleanAttribute {
    public GreedCatalystAttribute(Boolean value) {
        super(value);
    }

    @Override
    public Boolean getValue(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof InfusedCatalystItem)){
            return null;
        }

        return InfusedCatalystItem.isGreedy(itemStack);
    }

    public String getTranslationKey() {
        return "is_greed_catalyst";
    }
}
