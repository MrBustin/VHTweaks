package xyz.iwolfking.woldsvaults.integration.vaultfilters;

import net.joseph.vaultfilters.attributes.abstracts.StringAttribute;
import net.minecraft.world.item.ItemStack;

public class DeckCoreTierAttribute extends StringAttribute {
    public DeckCoreTierAttribute(String value) {
        super(value);
    }

    @Override
    public String getValue(ItemStack itemStack) {
        return DeckCoreFilterHelper.getCoreTier(itemStack).orElse(null);
    }

    @Override
    public String getNBTKey() {
        return "deck_core_tier";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[] { DeckCoreFilterHelper.displayValue(this.value) };
    }
}
