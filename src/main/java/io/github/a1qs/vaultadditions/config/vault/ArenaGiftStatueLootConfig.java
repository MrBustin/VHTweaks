package io.github.a1qs.vaultadditions.config.vault;

import iskallia.vault.config.entry.vending.ProductEntry;
import net.minecraft.world.item.Items;

public class ArenaGiftStatueLootConfig extends AbstractStatueLootConfig {
    protected void reset() {
        this.DROPS.clear();
        this.DROPS.add(new ProductEntry(Items.DIAMOND_ORE), 1);
        this.DROPS.add(new ProductEntry(Items.ANCIENT_DEBRIS), 1);
        this.DROPS.add(new ProductEntry(Items.RAW_IRON_BLOCK), 1);
        this.DROPS.add(new ProductEntry(Items.GILDED_BLACKSTONE), 1);
        this.DROPS.add(new ProductEntry(Items.RAW_GOLD_BLOCK), 1);
        this.DROPS.add(new ProductEntry(Items.CRYING_OBSIDIAN), 1);
        this.DROPS.add(new ProductEntry(Items.REDSTONE_BLOCK), 1);

        this.INTERVAL = 2500;
        this.MIN_ITEM_GENERATED = 32;
        this.MAX_ITEM_GENERATED = 64;
    }

    public String getName() {
        return "vaultadditions_statue_arena";
    }
}
