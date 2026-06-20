package xyz.iwolfking.woldsvaults.integration.vaultfilters;

import net.joseph.vaultfilters.attributes.abstracts.IntAttribute;
import net.minecraft.world.item.ItemStack;

public class DeckCoreSlotCountAttribute extends IntAttribute {
    public DeckCoreSlotCountAttribute(Integer value) {
        super(value == null ? 0 : value);
    }

    @Override
    public Integer getValue(ItemStack itemStack) {
        return DeckCoreFilterHelper.getSlotCount(itemStack).orElse(null);
    }

    @Override
    protected NumComparator getComparator() {
        return NumComparator.EQUAL;
    }

    @Override
    public String getNBTKey() {
        return "deck_core_slot_count";
    }
}
