package xyz.iwolfking.woldsvaults.integration.vaultfilters;

import iskallia.vault.item.VaultDollItem;
import iskallia.vault.world.data.DollLootData;
import net.joseph.vaultfilters.attributes.abstracts.BooleanAttribute;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class VaultDollCompletedAttribute extends BooleanAttribute {
    public VaultDollCompletedAttribute(Boolean value) {
        super(true);
    }

    @Override
    public Boolean getValue(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof VaultDollItem)) {
            return null;
        }

        if(!itemStack.hasTag()) {
            return null;
        }

        CompoundTag dollTag = itemStack.getTag();

        if(dollTag == null) {
            return null;
        }

        return dollTag.contains("completedBy");
    }

    public String getTranslationKey() {
        return "vault_doll_completed";
    }
}
