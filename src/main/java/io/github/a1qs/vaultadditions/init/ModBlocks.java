package io.github.a1qs.vaultadditions.init;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.block.*;
import iskallia.vault.block.PillarBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VaultAdditions.MOD_ID);


    public static final RegistryObject<Block> GLOBE_EXPANDER = registerBlock("globe_expander",
            () -> new GlobeExpanderBlock(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(-1, 3.6E8F).noOcclusion()),
            true);

    public static final RegistryObject<Block> EVENT_BLOCK = registerBlock("event_block",
            () -> new EventBlock(BlockBehaviour.Properties.of(Material.BARRIER)
                    .strength(-1, 3.6E8F).noOcclusion().noCollission().noDrops()),
            true);

    public static final RegistryObject<Block> LOOT_STATUE_VAULT = registerBlock("loot_statue_vault",
            () -> new LootStatueBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(3.0F, 3.6E8F).noOcclusion().requiresCorrectToolForDrops()),
            false);

    public static final RegistryObject<Block> LOOT_STATUE_GIFT = registerBlock("loot_statue_gift",
            () -> new LootStatueBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(3.0F, 3.6E8F).noOcclusion().requiresCorrectToolForDrops()),
            false);

    public static final RegistryObject<Block> LOOT_STATUE_GIFT_MEGA = registerBlock("loot_statue_gift_mega",
            () -> new LootStatueBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(3.0F, 3.6E8F).noOcclusion().requiresCorrectToolForDrops()),
            false);

    public static final RegistryObject<Block> LOOT_STATUE_ARENA = registerBlock("loot_statue_arena",
            () -> new LootStatueBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(3.0F, 3.6E8F).noOcclusion().requiresCorrectToolForDrops()),
            false);

    public static final RegistryObject<Block> STATUE_CAULDRON = registerBlock("statue_cauldron",
            () -> new StatueCauldronBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(3.0F, 3.6E8F).noOcclusion().requiresCorrectToolForDrops()),
            true);

    public static final RegistryObject<Block> PLAYER_TRADER = registerBlock("player_trader",
            () -> new PlayerTraderBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(3.0F, 3.6E8F).noOcclusion().requiresCorrectToolForDrops()),
            true);

    public static final RegistryObject<Block> EVENT_CONDITION_BLOCK = registerBlock("event_condition_block",
            () -> new EventConditionBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(-1, 3.6E8F).noOcclusion().requiresCorrectToolForDrops()),
            true);

    public static final RegistryObject<Block> EVENT_ACTIVE_BLOCK = registerBlock("event_active_block",
            () -> new EventActiveBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(-1, 3.6E8F).noOcclusion().requiresCorrectToolForDrops()),
            true);

    public static final RegistryObject<Block> POWER_BLOCK = registerBlock("power_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(1.5F, 1200.0F).noOcclusion().requiresCorrectToolForDrops()),
            true);


    public static final RegistryObject<Block> CUT_CALCITE_BRICKS = registerBlock("cut_calcite_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.CALCITE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> POLISHED_CUT_CALCITE_STAIRS = registerBlock("polished_cut_calcite_stairs_mimic",
            () -> new StairBlock(Blocks.CALCITE::defaultBlockState, BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.CALCITE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> RED_NETHER_BRICK_STAIRS = registerBlock("red_nether_brick_stairs_mimic",
            () -> new StairBlock(Blocks.RED_NETHER_BRICKS::defaultBlockState, BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> SMOOTH_SANDSTONE_STAIRS = registerBlock("smooth_sandstone_stairs_mimic",
            () -> new StairBlock(Blocks.SMOOTH_SANDSTONE::defaultBlockState, BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> BLUE_NETHER_BRICK_STAIRS = registerBlock("blue_nether_brick_stairs_mimic",
            () -> new StairBlock(Blocks.NETHER_BRICKS::defaultBlockState, BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.NETHER_BRICKS).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> VAULT_STONE_BRICK_STAIRS = registerBlock("vault_stone_brick_stairs_mimic",
            () -> new StairBlock(iskallia.vault.init.ModBlocks.VAULT_STONE_BRICKS::defaultBlockState, BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> POLISHED_CALCITE_PILLAR = registerBlock("polished_calcite_pillar_mimic",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.CALCITE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> GILDED_PILLAR = registerBlock("gilded_pillar_mimic",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> GILDED_BRICKS = registerBlock("gilded_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> SMOOTH_CRACKED_GILDED_BRICKS = registerBlock("smooth_cracked_gilded_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> SMOOTH_GILDED_BRICKS = registerBlock("smooth_gilded_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> IDONA_BRICKS = registerBlock("idona_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> IDONA_DARK_SMOOTH_BRICKS = registerBlock("idona_dark_smooth_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> IDONA_GEMMED_BLOCK = registerBlock("idona_gemmed_block_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> VELVET_ORNATE_PILLAR = registerBlock("velvet_ornate_pillar_mimic",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> ORNATE_BRICKS = registerBlock("ornate_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> CRACKED_ORNATE_BRICKS = registerBlock("cracked_ornate_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> VELVET_ORNATE_BRICKS = registerBlock("velvet_ornate_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> TENOS_BOOKSHELF = registerBlock("tenos_bookshelf_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOD).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> TENOS_BRICKS = registerBlock("tenos_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> TENOS_DARK_SMOOTH_BRICKS = registerBlock("tenos_dark_smooth_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> TENOS_GEMMED_BLOCK = registerBlock("tenos_gemmed_block_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> TENOS_LOG = registerBlock("tenos_log_mimic",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> VAULT_STONE_PILLAR = registerBlock("vault_stone_pillar_mimic",
            () -> new PillarBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> VELARA_BRICKS = registerBlock("velara_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> VELARA_DARK_SMOOTH_BRICKS = registerBlock("velara_dark_smooth_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> VELARA_GEMMED_BLOCK = registerBlock("velara_gemmed_block_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> VELARA_BLOOMING_LOG = registerBlock("velara_blooming_log_mimic",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> VELARA_STAIRS = registerBlock("velara_stairs_mimic",
            () -> new StairBlock(Blocks.STONE::defaultBlockState, BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> WENDARR_BRICKS = registerBlock("wendarr_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> WENDARR_DARK_SMOOTH_BRICKS = registerBlock("wendarr_dark_smooth_bricks_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> WENDARR_GEMMED_BLOCK = registerBlock("wendarr_gemmed_block_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> WENDARR_JEWELED_BLOCK = registerBlock("wendarr_jeweled_block_mimic",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> VECTOR_BLOCK = registerBlock("vector_plate_mimic",
            () -> new FacingCarpetBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2000000, 3.6E8F).noOcclusion()), true);

    public static final RegistryObject<Block> VELVET_BED_WHITE = registerBlock("colored_velvet_bed_white", () -> bed(DyeColor.WHITE), false);
    public static final RegistryObject<Block> VELVET_BED_ORANGE = registerBlock("colored_velvet_bed_orange", () -> bed(DyeColor.ORANGE), false);
    public static final RegistryObject<Block> VELVET_BED_MAGENTA = registerBlock("colored_velvet_bed_magenta", () -> bed(DyeColor.MAGENTA), false);
    public static final RegistryObject<Block> VELVET_BED_LIGHT_BLUE = registerBlock("colored_velvet_bed_light_blue", () -> bed(DyeColor.LIGHT_BLUE), false);
    public static final RegistryObject<Block> VELVET_BED_YELLOW = registerBlock("colored_velvet_bed_yellow", () -> bed(DyeColor.YELLOW), false);
    public static final RegistryObject<Block> VELVET_BED_LIME = registerBlock("colored_velvet_bed_lime", () -> bed(DyeColor.LIME), false);
    public static final RegistryObject<Block> VELVET_BED_PINK = registerBlock("colored_velvet_bed_pink", () -> bed(DyeColor.PINK), false);
    public static final RegistryObject<Block> VELVET_BED_GRAY = registerBlock("colored_velvet_bed_gray", () -> bed(DyeColor.GRAY), false);
    public static final RegistryObject<Block> VELVET_BED_LIGHT_GRAY = registerBlock("colored_velvet_bed_light_gray", () -> bed(DyeColor.LIGHT_GRAY), false);
    public static final RegistryObject<Block> VELVET_BED_CYAN = registerBlock("colored_velvet_bed_cyan", () -> bed(DyeColor.CYAN), false);
    public static final RegistryObject<Block> VELVET_BED_PURPLE = registerBlock("colored_velvet_bed_purple", () -> bed(DyeColor.PURPLE), false);
    public static final RegistryObject<Block> VELVET_BED_BLUE = registerBlock("colored_velvet_bed_blue", () -> bed(DyeColor.BLUE), false);
    public static final RegistryObject<Block> VELVET_BED_BROWN = registerBlock("colored_velvet_bed_brown", () -> bed(DyeColor.BROWN), false);
    public static final RegistryObject<Block> VELVET_BED_GREEN = registerBlock("colored_velvet_bed_green", () -> bed(DyeColor.GREEN), false);
    public static final RegistryObject<Block> VELVET_BED_RED = registerBlock("colored_velvet_bed_red", () -> bed(DyeColor.RED), false);
    public static final RegistryObject<Block> VELVET_BED_BLACK = registerBlock("colored_velvet_bed_black", () -> bed(DyeColor.BLACK), false);

    public static final RegistryObject<Block> FOOLS_GOLD = registerBlock("fools_gold",
            () -> new RotateableFoolsGold(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0F, 3600000.0F).noOcclusion()), true);
    public static final RegistryObject<Block> ENCHANTED_FIRE = registerBlock("enchanted_fire",
            () -> new EnchantedFireBlock(BlockBehaviour.Properties.of(Material.FIRE, MaterialColor.COLOR_PURPLE).noCollission().instabreak()
                    .lightLevel(state -> 7).sound(SoundType.WOOL).noDrops()), true);

    public static final RegistryObject<RaidPlaqueBlock> RAID_PLAQUE = registerBlock("raid_plaque", RaidPlaqueBlock::new, false);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, boolean createBlockItem) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        if(createBlockItem) registerBlockItem(name, toReturn, VaultAdditions.VAULT_ADDITIONS_TAB);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
                                                                            CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(tab)));
    }


    private static ColoredVelvetBed bed(DyeColor pColor) {
        return new ColoredVelvetBed(pColor, BlockBehaviour.Properties.of(Material.WOOL,
                (pBlockState) -> pBlockState.getValue(BedBlock.PART) == BedPart.FOOT ? pColor.getMaterialColor() : MaterialColor.WOOL).sound(SoundType.WOOD).strength(0.2F).noOcclusion());
    }


}
