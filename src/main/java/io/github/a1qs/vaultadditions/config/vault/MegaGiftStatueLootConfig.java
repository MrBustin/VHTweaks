package io.github.a1qs.vaultadditions.config.vault;

import iskallia.vault.config.entry.vending.ProductEntry;
import net.minecraft.world.item.Items;

public class MegaGiftStatueLootConfig extends AbstractStatueLootConfig {
    protected void reset() {
        this.DROPS.clear();
        this.DROPS.add(new ProductEntry(Items.AMETHYST_BLOCK), 1);
        this.DROPS.add(new ProductEntry(Items.NETHER_GOLD_ORE), 1);
        this.DROPS.add(new ProductEntry(Items.NETHER_QUARTZ_ORE), 1);
        this.DROPS.add(new ProductEntry(Items.COAL_BLOCK), 1);
        this.DROPS.add(new ProductEntry(Items.CRIMSON_STEM), 1);
        this.DROPS.add(new ProductEntry(Items.WARPED_STEM), 1);
        this.DROPS.add(new ProductEntry(Items.SPONGE), 1);
        this.DROPS.add(new ProductEntry(Items.GLASS), 1);
        this.DROPS.add(new ProductEntry(Items.WHITE_WOOL), 1);
        this.DROPS.add(new ProductEntry(Items.PURPUR_BLOCK), 1);
        this.DROPS.add(new ProductEntry(Items.ICE), 1);
        this.DROPS.add(new ProductEntry(Items.OBSIDIAN), 1);
        this.DROPS.add(new ProductEntry(Items.COBWEB), 1);
        this.DROPS.add(new ProductEntry(Items.RAW_COPPER_BLOCK), 1);
        this.DROPS.add(new ProductEntry(Items.GLOWSTONE), 1);
        this.DROPS.add(new ProductEntry(Items.HAY_BLOCK), 1);
        this.DROPS.add(new ProductEntry(Items.SEA_LANTERN), 1);
        this.DROPS.add(new ProductEntry(Items.BONE_BLOCK), 1);
        this.DROPS.add(new ProductEntry(Items.DEAD_TUBE_CORAL_BLOCK), 1);
        this.DROPS.add(new ProductEntry(Items.MAGMA_BLOCK), 1);
        this.DROPS.add(new ProductEntry(Items.CHORUS_FLOWER), 1);

        this.INTERVAL = 750;
        this.MIN_ITEM_GENERATED = 128;
        this.MAX_ITEM_GENERATED = 256;
    }

    public String getName() {
        return "vaultadditions_statue_mega_gift";
    }
}
