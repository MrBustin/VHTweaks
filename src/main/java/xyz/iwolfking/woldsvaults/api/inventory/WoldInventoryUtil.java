package xyz.iwolfking.woldsvaults.api.inventory;

import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import xyz.iwolfking.woldsvaults.items.ItemScavengerPouch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WoldInventoryUtil {

    public static List<InventoryUtil.ItemAccess> getScavPouchAccess(InventoryUtil.ItemAccess access) {
        if (!(access.getItem() instanceof ItemScavengerPouch)) {
            return Collections.emptyList();
        } else {
            OverSizedInventory contents = ItemScavengerPouch.getInventory(access.getStack());
            List<InventoryUtil.ItemAccess> accesses = new ArrayList<>();

            for(int slot = 0; slot < contents.getContainerSize(); ++slot) {
                ItemStack stack = contents.getItem(slot);
                if (!stack.isEmpty()) {
                    int finalSlot = slot;
                    accesses.add(access.chain(stack, (containerStack, newStack) -> {
                        OverSizedInventory ctContents = ItemScavengerPouch.getInventory(containerStack);
                        ctContents.setItem(finalSlot, newStack);
                    }));
                }
            }

            return accesses;
        }
    }

}
