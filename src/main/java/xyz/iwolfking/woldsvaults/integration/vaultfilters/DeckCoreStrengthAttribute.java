package xyz.iwolfking.woldsvaults.integration.vaultfilters;

import net.joseph.vaultfilters.attributes.abstracts.IntAttribute;
import net.minecraft.world.item.ItemStack;

public class DeckCoreStrengthAttribute extends IntAttribute {
    public DeckCoreStrengthAttribute(Integer value) {
        super(value == null ? 0 : value);
    }

    @Override
    public Integer getValue(ItemStack itemStack) {
        return DeckCoreFilterHelper.getStrengthPercent(itemStack).orElse(null);
    }

    @Override
    protected NumComparator getComparator() {
        return NumComparator.AT_LEAST;
    }

    @Override
    public String getNBTKey() {
        return "deck_core_strength";
    }
}
