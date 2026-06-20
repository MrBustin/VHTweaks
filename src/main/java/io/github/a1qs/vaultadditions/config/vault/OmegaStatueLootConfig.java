package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.vending.ProductEntry;
import net.minecraft.world.item.Items;

import java.util.HashMap;

public class OmegaStatueLootConfig extends AbstractStatueLootConfig {
    @Expose
    private int MAX_ACCELERATION_CHIPS;
    @Expose
    private HashMap<Integer, Integer> INTERVAL_DECREASE_PER_CHIP = new HashMap<>();

    protected void reset() {
        this.DROPS.clear();
        this.DROPS.add(new ProductEntry(Items.STONE), 1);
        this.DROPS.add(new ProductEntry(Items.GRANITE), 1);
        this.DROPS.add(new ProductEntry(Items.DIORITE), 3);
        this.DROPS.add(new ProductEntry(Items.DIRT), 3);
        this.DROPS.add(new ProductEntry(Items.GRASS_BLOCK), 3);
        this.DROPS.add(new ProductEntry(Items.GRAVEL), 1);
        this.DROPS.add(new ProductEntry(Items.SAND), 1);
        this.DROPS.add(new ProductEntry(Items.COBBLESTONE), 3);
        this.DROPS.add(new ProductEntry(Items.OAK_LOG), 3);
        this.DROPS.add(new ProductEntry(Items.OAK_LEAVES), 5);
        this.DROPS.add(new ProductEntry(Items.SPRUCE_LEAVES), 3);
        this.DROPS.add(new ProductEntry(Items.BIRCH_LEAVES), 3);
        this.DROPS.add(new ProductEntry(Items.NETHERRACK), 3);
        this.DROPS.add(new ProductEntry(Items.BASALT), 3);
        this.DROPS.add(new ProductEntry(Items.MOSSY_COBBLESTONE), 3);
        this.DROPS.add(new ProductEntry(Items.CLAY), 3);
        this.DROPS.add(new ProductEntry(Items.TERRACOTTA), 3);
        this.DROPS.add(new ProductEntry(Items.PUMPKIN), 3);
        this.DROPS.add(new ProductEntry(Items.MELON), 3);
        this.DROPS.add(new ProductEntry(Items.DEEPSLATE), 3);
        this.DROPS.add(new ProductEntry(Items.TUFF), 3);
        this.DROPS.add(new ProductEntry(Items.CRIMSON_NYLIUM), 3);
        this.DROPS.add(new ProductEntry(Items.WARPED_NYLIUM), 3);
        this.DROPS.add(new ProductEntry(Items.COAL_ORE), 1);
        this.DROPS.add(new ProductEntry(Items.LAPIS_ORE), 1);
        this.DROPS.add(new ProductEntry(Items.JUNGLE_LOG), 3);
        this.DROPS.add(new ProductEntry(Items.ACACIA_LOG), 3);
        this.DROPS.add(new ProductEntry(Items.SPRUCE_LOG), 3);
        this.DROPS.add(new ProductEntry(Items.JUNGLE_LEAVES), 3);
        this.DROPS.add(new ProductEntry(Items.FLOWERING_AZALEA_LEAVES), 3);
        this.DROPS.add(new ProductEntry(Items.SEA_PICKLE), 2);
        this.DROPS.add(new ProductEntry(Items.DEAD_BUSH), 2);
        this.DROPS.add(new ProductEntry(Items.MYCELIUM), 3);
        this.DROPS.add(new ProductEntry(Items.MOSSY_STONE_BRICKS), 3);
        this.DROPS.add(new ProductEntry(Items.SNOW_BLOCK), 3);
        this.DROPS.add(new ProductEntry(Items.SOUL_SAND), 3);
        this.DROPS.add(new ProductEntry(Items.WHITE_CONCRETE), 3);
        this.DROPS.add(new ProductEntry(Items.CHORUS_FLOWER), 2);
        this.DROPS.add(new ProductEntry(Items.SEA_LANTERN), 2);
        this.DROPS.add(new ProductEntry(Items.END_STONE), 3);
        this.DROPS.add(new ProductEntry(Items.MAGMA_BLOCK), 3);
        this.DROPS.add(new ProductEntry(Items.WHITE_WOOL), 2);
        this.DROPS.add(new ProductEntry(Items.CRIMSON_STEM), 3);
        this.DROPS.add(new ProductEntry(Items.WARPED_STEM), 3);

        this.INTERVAL = 1200;
        this.MIN_ITEM_GENERATED = 0;
        this.MAX_ITEM_GENERATED = 0;

        this.MAX_ACCELERATION_CHIPS = 3;
        this.INTERVAL_DECREASE_PER_CHIP.put(1, 266);
        this.INTERVAL_DECREASE_PER_CHIP.put(2, 533);
        this.INTERVAL_DECREASE_PER_CHIP.put(3, 800);
    }

    public int getMaxAccelerationChips() {
        return this.MAX_ACCELERATION_CHIPS;
    }

    public int getIntervalDecrease(int chipCount) {
        return this.INTERVAL_DECREASE_PER_CHIP.get(chipCount);
    }

    public String getName() {
        return "vaultadditions_statue_omega";
    }
}
