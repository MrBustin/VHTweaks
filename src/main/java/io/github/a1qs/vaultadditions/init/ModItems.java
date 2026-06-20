package io.github.a1qs.vaultadditions.init;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.item.ColoredVelvetBedItem;
import io.github.a1qs.vaultadditions.item.LootStatueBlockItem;
import io.github.a1qs.vaultadditions.item.PowerCrystal;
import io.github.a1qs.vaultadditions.item.PowerOrb;
import io.github.a1qs.vaultadditions.item.RaidPlaqueBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VaultAdditions.MOD_ID);

    public static final RegistryObject<Item> POWER_CRYSTAL = ITEMS.register("power_crystal",
            () -> new PowerCrystal(new Item.Properties().tab(VaultAdditions.VAULT_ADDITIONS_TAB)));

    public static final RegistryObject<Item> POWER_ORB = ITEMS.register("power_orb",
            () -> new PowerOrb(new Item.Properties().tab(VaultAdditions.VAULT_ADDITIONS_TAB)));

    public static final RegistryObject<Item> DAMAGE_CORE = ITEMS.register("damage_core",
            () -> new Item(new Item.Properties().tab(VaultAdditions.VAULT_ADDITIONS_TAB)));

    public static final RegistryObject<Item> FRACTURE_CORE = ITEMS.register("fracture_core",
            () -> new Item(new Item.Properties().tab(VaultAdditions.VAULT_ADDITIONS_TAB)));

    public static final RegistryObject<Item> LOOT_STATUE_VAULT = ModItems.ITEMS.register("loot_statue_vault",
            () -> new LootStatueBlockItem(ModBlocks.LOOT_STATUE_VAULT.get()));

    public static final RegistryObject<Item> LOOT_STATUE_GIFT = ModItems.ITEMS.register("loot_statue_gift",
            () -> new LootStatueBlockItem(ModBlocks.LOOT_STATUE_GIFT.get()));

    public static final RegistryObject<Item> LOOT_STATUE_GIFT_MEGA = ModItems.ITEMS.register("loot_statue_gift_mega",
            () -> new LootStatueBlockItem(ModBlocks.LOOT_STATUE_GIFT_MEGA.get()));

    public static final RegistryObject<Item> LOOT_STATUE_ARENA = ModItems.ITEMS.register("loot_statue_arena",
            () -> new LootStatueBlockItem(ModBlocks.LOOT_STATUE_ARENA.get()));

    public static final RegistryObject<Item> SHIELD = ModItems.ITEMS.register("shield",
            () -> new Item(new Item.Properties().tab(VaultAdditions.VAULT_ADDITIONS_TAB)));

    public static final RegistryObject<Item> RAID_PLAQUE_BLOCK_ITEM = ITEMS.register("raid_plaque",
            () -> new RaidPlaqueBlockItem(ModBlocks.RAID_PLAQUE.get()));

    public static final RegistryObject<Item> POWER_DUST = ITEMS.register("power_dust",
            ()->  new Item(new Item.Properties().tab(VaultAdditions.VAULT_ADDITIONS_TAB)));

    public static final RegistryObject<Item> POWER_SHARD = ITEMS.register("power_shard",
            ()->  new Item(new Item.Properties().tab(VaultAdditions.VAULT_ADDITIONS_TAB)));

    public static final RegistryObject<Item> VELVET_BED_ITEM_WHITE = registerBedItem("colored_velvet_bed_white", ModBlocks.VELVET_BED_WHITE);
    public static final RegistryObject<Item> VELVET_BED_ITEM_ORANGE = registerBedItem("colored_velvet_bed_orange", ModBlocks.VELVET_BED_ORANGE);
    public static final RegistryObject<Item> VELVET_BED_ITEM_MAGENTA = registerBedItem("colored_velvet_bed_magenta", ModBlocks.VELVET_BED_MAGENTA);
    public static final RegistryObject<Item> VELVET_BED_ITEM_LIGHT_BLUE = registerBedItem("colored_velvet_bed_light_blue", ModBlocks.VELVET_BED_LIGHT_BLUE);
    public static final RegistryObject<Item> VELVET_BED_ITEM_YELLOW = registerBedItem("colored_velvet_bed_yellow", ModBlocks.VELVET_BED_YELLOW);
    public static final RegistryObject<Item> VELVET_BED_ITEM_LIME = registerBedItem("colored_velvet_bed_lime", ModBlocks.VELVET_BED_LIME);
    public static final RegistryObject<Item> VELVET_BED_ITEM_PINK = registerBedItem("colored_velvet_bed_pink", ModBlocks.VELVET_BED_PINK);
    public static final RegistryObject<Item> VELVET_BED_ITEM_GRAY = registerBedItem("colored_velvet_bed_gray", ModBlocks.VELVET_BED_GRAY);
    public static final RegistryObject<Item> VELVET_BED_ITEM_LIGHT_GRAY = registerBedItem("colored_velvet_bed_light_gray", ModBlocks.VELVET_BED_LIGHT_GRAY);
    public static final RegistryObject<Item> VELVET_BED_ITEM_CYAN = registerBedItem("colored_velvet_bed_cyan", ModBlocks.VELVET_BED_CYAN);
    public static final RegistryObject<Item> VELVET_BED_ITEM_PURPLE = registerBedItem("colored_velvet_bed_purple", ModBlocks.VELVET_BED_PURPLE);
    public static final RegistryObject<Item> VELVET_BED_ITEM_BLUE = registerBedItem("colored_velvet_bed_blue", ModBlocks.VELVET_BED_BLUE);
    public static final RegistryObject<Item> VELVET_BED_ITEM_BROWN = registerBedItem("colored_velvet_bed_brown", ModBlocks.VELVET_BED_BROWN);
    public static final RegistryObject<Item> VELVET_BED_ITEM_GREEN = registerBedItem("colored_velvet_bed_green", ModBlocks.VELVET_BED_GREEN);
    public static final RegistryObject<Item> VELVET_BED_ITEM_RED = registerBedItem("colored_velvet_bed_red", ModBlocks.VELVET_BED_RED);
    public static final RegistryObject<Item> VELVET_BED_ITEM_BLACK = registerBedItem("colored_velvet_bed_black", ModBlocks.VELVET_BED_BLACK);

    private static RegistryObject<Item> registerBedItem(String name, RegistryObject<Block> block) {
        return ITEMS.register(name,
                () -> new ColoredVelvetBedItem(block.get(), new Item.Properties().tab(VaultAdditions.VAULT_ADDITIONS_TAB).stacksTo(1)));
    }
}
