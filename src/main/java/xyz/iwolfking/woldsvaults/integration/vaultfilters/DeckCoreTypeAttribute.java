package xyz.iwolfking.woldsvaults.integration.vaultfilters;

import net.joseph.vaultfilters.attributes.abstracts.StringAttribute;
import net.minecraft.world.item.ItemStack;

public class DeckCoreTypeAttribute extends StringAttribute {
    public DeckCoreTypeAttribute(String value) {
        super(value);
    }

    @Override
    public String getValue(ItemStack itemStack) {
        return DeckCoreFilterHelper.getCoreId(itemStack).orElse(null);
    }

    @Override
    public String getNBTKey() {
        return "deck_core_type";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[] { DeckCoreFilterHelper.getCoreDisplayName(this.value) };
    }
}
