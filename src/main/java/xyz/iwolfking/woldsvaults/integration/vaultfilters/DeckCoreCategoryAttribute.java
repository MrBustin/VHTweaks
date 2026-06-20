package xyz.iwolfking.woldsvaults.integration.vaultfilters;

import java.util.List;
import net.joseph.vaultfilters.attributes.abstracts.StringListAttribute;
import net.minecraft.world.item.ItemStack;

public class DeckCoreCategoryAttribute extends StringListAttribute {
    public DeckCoreCategoryAttribute(String value) {
        super(value);
    }

    @Override
    public String getValue(ItemStack itemStack) {
        List<String> values = getValues(itemStack);
        return values.isEmpty() ? null : values.get(0);
    }

    @Override
    public List<String> getValues(ItemStack itemStack) {
        return DeckCoreFilterHelper.getCategories(itemStack);
    }

    @Override
    public String getNBTKey() {
        return "deck_core_category";
    }
}
