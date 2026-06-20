package xyz.iwolfking.woldsvaults.integration.vaultfilters;

import iskallia.vault.item.InscriptionItem;
import iskallia.vault.item.data.InscriptionData;
import net.joseph.vaultfilters.attributes.abstracts.BooleanAttribute;
import net.minecraft.world.item.ItemStack;

public class SuperInscriptionAttribute extends BooleanAttribute {
    public SuperInscriptionAttribute(Boolean value) {
        super(value);
    }

    @Override
    public Boolean getValue(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof InscriptionItem)){
            return null;
        }

        return InscriptionData.from(itemStack).isSuper();
    }

    @Override
    public String getNBTKey() {
        return "is_super_inscription";
    }

    @Override
    public String getTranslationKey() {
        return "is_super_inscription";
    }
}
