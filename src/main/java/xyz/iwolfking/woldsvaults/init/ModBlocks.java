package xyz.iwolfking.woldsvaults.init;

import com.mojang.datafixers.types.Type;
import iskallia.vault.VaultMod;
import iskallia.vault.block.*;
import iskallia.vault.block.entity.CardboardBoxTileEntity;
import iskallia.vault.block.render.ChallengeControllerRenderer;
import iskallia.vault.block.render.ScavengerAltarRenderer;
import iskallia.vault.item.CoinBlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.blocks.*;
import xyz.iwolfking.woldsvaults.blocks.LootStatueBlock;
import xyz.iwolfking.woldsvaults.blocks.challenge.ProtectVillagerControllerProxyBlock;
import xyz.iwolfking.woldsvaults.blocks.challenge.TempleControllerProxyBlock;
import xyz.iwolfking.woldsvaults.blocks.challenge.TempleControllerBlock;
import xyz.iwolfking.woldsvaults.blocks.renderers.challenge.TempleControllerRenderer;
import xyz.iwolfking.woldsvaults.blocks.renderers.challenge.ProtectVillagerControllerProxyRenderer;
import xyz.iwolfking.woldsvaults.blocks.renderers.challenge.TempleControllerProxyRenderer;
import xyz.iwolfking.woldsvaults.blocks.tiles.challenge.ProtectVillagerControllerProxyBlockEntity;
import xyz.iwolfking.woldsvaults.blocks.tiles.challenge.TempleControllerProxyBlockEntity;
import xyz.iwolfking.woldsvaults.blocks.tiles.challenge.ProtectVillagerControllerTileEntity;
import xyz.iwolfking.woldsvaults.blocks.tiles.challenge.TempleControllerTileEntity;
import xyz.iwolfking.woldsvaults.client.renderers.BrewingAltarRenderer;
import xyz.iwolfking.woldsvaults.blocks.renderers.DecoGridGatewayRenderer;
import xyz.iwolfking.woldsvaults.blocks.renderers.DungeonPedestalRenderer;
import xyz.iwolfking.woldsvaults.blocks.renderers.SurvivalMobBarrierRenderer;
import xyz.iwolfking.woldsvaults.blocks.tiles.*;

import java.util.function.Consumer;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Supplier;

public class ModBlocks {

    public static final Block CHROMATIC_GOLD_BLOCK;
    public static final Block OMEGA_POG_BLOCK;
    public static final Block ECHO_POG_BLOCK;
    public static final Block GEM_POG_BLOCK;
    public static final Block COMPRESSED_VAULT_DIAMOND_BLOCK;
    public static final Block COMPRESSED_VAULT_ESSENCE;
    public static final Block COMPRESSED_VAULT_ESSENCE_2;
    public static final Block COMPRESSED_VAULT_CARBON;
    public static final Block KNOWLEDGE_STAR_BLOCK;


    public static final HellishSandBlock WENDARR_SAND;
    public static final HellishSandBlock TENOS_SAND;
    public static final HellishSandBlock VELARA_SAND;
    public static final HellishSandBlock IDONA_SAND;

    public static final CardboardBoxBlock RED_CARDBOARD_BOX;
    public static final CardboardBoxBlock YELLOW_CARDBOARD_BOX;
    public static final CardboardBoxBlock GREEN_CARDBOARD_BOX;
    public static final CardboardBoxBlock BLUE_CARDBOARD_BOX;


    public static final VaultSalvagerBlock VAULT_SALVAGER_BLOCK;
    public static final IskallianLeavesBlock ISKALLIAN_LEAVES_BLOCK;
    public static final HellishSandBlock HELLISH_SAND_BLOCK;
    public static final DungeonPedestalBlock DUNGEON_PEDESTAL_BLOCK;
    public static final DecoScavengerAltarBlock DECO_SCAVENGER_ALTAR_BLOCK;
    public static final DecoBossRunePillarBlock DECO_BOSS_RUNE_PILLAR_BLOCK;
    public static final DecoObeliskBlock DECO_OBELISK_BLOCK;
    public static final DecoLodestoneBlock DECO_LODESTONE_BLOCK;
    public static final DecoMonolithBlock DECO_MONOLITH_BLOCK;
    public static final SurvivalMobBarrier SURVIVAL_MOB_BARRIER;
    public static final DecoGridGatewayBlock DECO_GRID_GATEWAY_BLOCK;
    public static final CoinPileDecorBlock VAULT_PALLADIUM_PILE;
    public static final CoinPileDecorBlock VAULT_IRIDIUM_PILE;
    public static final BrewingAltar BREWING_ALTAR;
    public static final VaultCrateBlock VAULT_CRATE_ALCHEMY;
    public static final VaultCrateBlock VAULT_CRATE_BRUTAL_BOSSES;
    public static final VaultCrateBlock VAULT_CRATE_HAUNTED_BRAZIERS;
    public static final VaultCrateBlock VAULT_CRATE_BALLISTIC_BINGO;
    public static final VaultCrateBlock VAULT_CRATE_UNHINGED_SCAVENGER;
    public static final BreakableCrateBlock CRYSTALLIZED_STONE;
    public static BlockItem VAULT_PALLADIUM;
    public static BlockItem VAULT_IRIDIUM;

    public static final DecoIdonaAltarBlock DECO_IDONA_ALTAR_BLOCK;
    public static final DecoTenosAltarBlock DECO_TENOS_ALTAR_BLOCK;
    public static final DecoWendarrAltarBlock DECO_WENDARR_ALTAR_BLOCK;
    public static final DecoVelaraAltarBlock DECO_VELARA_ALTAR_BLOCK;

    public static final ProtectVillagerControllerProxyBlock PROTECT_VILLAGER_CONTROLLER_PROXY;
    public static final TempleControllerProxyBlock TEMPLE_CONTROLLER_PROXY;
    public static final ChallengeControllerBlock PROTECT_VILLAGER_CONTROLLER;
    public static final TempleControllerBlock TEMPLE_CONTROLLER;

    public static final BlockEntityType<VaultSalvagerTileEntity> VAULT_SALVAGER_ENTITY;
    public static final BlockEntityType<IskallianLeavesTileEntity> ISKALLIAN_LEAVES_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<HellishSandTileEntity> HELLISH_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<WendarrSandTileEntity> WENDARR_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<TenosSandTileEntity> TENOS_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<VelaraSandTileEntity> VELARA_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<IdonaSandTileEntity> IDONA_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<DungeonPedestalTileEntity> DUNGEON_PEDESTAL_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<DecoScavengerAltarEntity> DECO_SCAVENGER_ALTAR_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<DecoBossRunePillarEntity> DECO_BOSS_RUNE_PILLAR_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<DecoObeliskTileEntity> DECO_OBELISK_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<DecoLodestoneTileEntity> DECO_LODESTONE_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<DecoMonolithTileEntity> DECO_MONOLITH_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<SurvivalMobBarrierTileEntity> SURVIVAL_MOB_BARRIER_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<DecoGridGatewayTileEntity> DECO_GRID_GATEWAY_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<DecoIdonaAltarTileEntity> DECO_IDONA_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<DecoTenosAltarTileEntity> DECO_TENOS_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<DecoWendarrAltarTileEntity> DECO_WENDARR_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<DecoVelaraAltarTileEntity> DECO_VELARA_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<BrewingAltarTileEntity> BREWING_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    public static final BlockEntityType<BackpackBlockEntity> SOPHISTICATED_BACKPACK;
    public static final BlockEntityType<CardboardBoxTileEntity> RED_CARDBOARD_BOX_TILE_ENTITY;
    public static final BlockEntityType<CardboardBoxTileEntity> YELLOW_CARDBOARD_BOX_TILE_ENTITY;
    public static final BlockEntityType<CardboardBoxTileEntity> GREEN_CARDBOARD_BOX_TILE_ENTITY;
    public static final BlockEntityType<CardboardBoxTileEntity> BLUE_CARDBOARD_BOX_TILE_ENTITY;

    public static final BackpackBlock XL_BACKPACK;

    static {
        VAULT_PALLADIUM_PILE = new CoinPileDecorBlock();
        VAULT_IRIDIUM_PILE = new CoinPileDecorBlock();
        BREWING_ALTAR = new BrewingAltar();
        VAULT_CRATE_ALCHEMY = new VaultCrateBlock();
        VAULT_CRATE_BRUTAL_BOSSES = new VaultCrateBlock();
        VAULT_CRATE_HAUNTED_BRAZIERS = new VaultCrateBlock();
        VAULT_CRATE_BALLISTIC_BINGO = new VaultCrateBlock();
        VAULT_CRATE_UNHINGED_SCAVENGER = new VaultCrateBlock();
        CRYSTALLIZED_STONE = new BreakableCrateBlock();
        VAULT_PALLADIUM  = new CoinBlockItem(VAULT_PALLADIUM_PILE, new Item.Properties().tab(iskallia.vault.init.ModItems.VAULT_MOD_GROUP));
        VAULT_IRIDIUM  = new CoinBlockItem(VAULT_IRIDIUM_PILE, new Item.Properties().tab(iskallia.vault.init.ModItems.VAULT_MOD_GROUP));
        VAULT_SALVAGER_BLOCK = new VaultSalvagerBlock();
        ISKALLIAN_LEAVES_BLOCK = new IskallianLeavesBlock();
        HELLISH_SAND_BLOCK = new HellishSandBlock();
        WENDARR_SAND = new HellishSandBlock(ModBlocks::getWendarrSandTileEntityType);
        TENOS_SAND = new HellishSandBlock(ModBlocks::getTenosSandTileEntityType);
        VELARA_SAND = new HellishSandBlock(ModBlocks::getVelaraSandTileEntityType);
        IDONA_SAND = new HellishSandBlock(ModBlocks::getIdonaSandTileEntityType);
        DUNGEON_PEDESTAL_BLOCK = new DungeonPedestalBlock();
        DECO_SCAVENGER_ALTAR_BLOCK = new DecoScavengerAltarBlock();
        DECO_BOSS_RUNE_PILLAR_BLOCK = new DecoBossRunePillarBlock();
        DECO_OBELISK_BLOCK = new DecoObeliskBlock();
        DECO_LODESTONE_BLOCK = new DecoLodestoneBlock();
        DECO_MONOLITH_BLOCK = new DecoMonolithBlock();
        SURVIVAL_MOB_BARRIER = new SurvivalMobBarrier();
        DECO_GRID_GATEWAY_BLOCK = new DecoGridGatewayBlock();
        DECO_IDONA_ALTAR_BLOCK = new DecoIdonaAltarBlock();
        DECO_TENOS_ALTAR_BLOCK = new DecoTenosAltarBlock();
        DECO_WENDARR_ALTAR_BLOCK = new DecoWendarrAltarBlock();
        DECO_VELARA_ALTAR_BLOCK = new DecoVelaraAltarBlock();
        XL_BACKPACK = new BackpackBlock(12000);
        CHROMATIC_GOLD_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK).strength(3.0F, 6.0F));
        OMEGA_POG_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(9.0F, 60000.0F));
        ECHO_POG_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(6.0F, 40000.0F));
        GEM_POG_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(3.0F, 1000.0F));
        COMPRESSED_VAULT_CARBON = new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK).strength(3.0F, 6.0F));
        COMPRESSED_VAULT_DIAMOND_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).strength(5.0F, 500.0F));
        COMPRESSED_VAULT_ESSENCE = new Block(BlockBehaviour.Properties.copy(Blocks.SAND).strength(8.0F, 200.0F));
        COMPRESSED_VAULT_ESSENCE_2 = new Block(BlockBehaviour.Properties.copy(Blocks.SAND).strength(10.0F, 200.0F));
        KNOWLEDGE_STAR_BLOCK = new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK).strength(3.0F, 6.0F));
        VAULT_SALVAGER_ENTITY = BlockEntityType.Builder.of(VaultSalvagerTileEntity::new, new Block[]{VAULT_SALVAGER_BLOCK}).build((Type)null);
        ISKALLIAN_LEAVES_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(IskallianLeavesTileEntity::new, new Block[]{ISKALLIAN_LEAVES_BLOCK}).build((Type)null);
        HELLISH_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(HellishSandTileEntity::new, new Block[]{HELLISH_SAND_BLOCK}).build((Type)null);
        WENDARR_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(WendarrSandTileEntity::new, new Block[]{WENDARR_SAND}).build((Type)null);
        TENOS_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(TenosSandTileEntity::new, new Block[]{TENOS_SAND}).build((Type)null);
        VELARA_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(VelaraSandTileEntity::new, new Block[]{VELARA_SAND}).build((Type)null);
        IDONA_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(IdonaSandTileEntity::new, new Block[]{IDONA_SAND}).build((Type)null);
        DUNGEON_PEDESTAL_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(DungeonPedestalTileEntity::new, new Block[]{DUNGEON_PEDESTAL_BLOCK}).build((Type)null);
        DECO_SCAVENGER_ALTAR_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(DecoScavengerAltarEntity::new, new Block[]{DECO_SCAVENGER_ALTAR_BLOCK}).build((Type)null);
        DECO_BOSS_RUNE_PILLAR_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(DecoBossRunePillarEntity::new, new Block[]{DECO_BOSS_RUNE_PILLAR_BLOCK}).build((Type)null);
        DECO_OBELISK_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(DecoObeliskTileEntity::new, new Block[]{DECO_OBELISK_BLOCK}).build((Type)null);
        DECO_LODESTONE_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(DecoLodestoneTileEntity::new, new Block[]{DECO_LODESTONE_BLOCK}).build((Type)null);
        DECO_MONOLITH_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(DecoMonolithTileEntity::new, new Block[]{DECO_MONOLITH_BLOCK}).build((Type)null);
        SURVIVAL_MOB_BARRIER_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(SurvivalMobBarrierTileEntity::new, new Block[]{SURVIVAL_MOB_BARRIER}).build((Type)null);
        DECO_GRID_GATEWAY_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(DecoGridGatewayTileEntity::new, new Block[]{DECO_GRID_GATEWAY_BLOCK}).build((Type)null);
        DECO_IDONA_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(DecoIdonaAltarTileEntity::new, new Block[]{DECO_IDONA_ALTAR_BLOCK}).build((Type)null);
        DECO_TENOS_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(DecoTenosAltarTileEntity::new, new Block[]{DECO_TENOS_ALTAR_BLOCK}).build((Type)null);
        DECO_WENDARR_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(DecoWendarrAltarTileEntity::new, new Block[]{DECO_WENDARR_ALTAR_BLOCK}).build((Type)null);
        DECO_VELARA_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(DecoVelaraAltarTileEntity::new, new Block[]{DECO_VELARA_ALTAR_BLOCK}).build((Type)null);
        BREWING_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.of(BrewingAltarTileEntity::new, new Block[]{BREWING_ALTAR}).build((Type)null);
        SOPHISTICATED_BACKPACK = BlockEntityType.Builder.of(BackpackBlockEntity::new, new Block[]{XL_BACKPACK}).build((Type)null);

        PROTECT_VILLAGER_CONTROLLER_PROXY = new ProtectVillagerControllerProxyBlock();
        TEMPLE_CONTROLLER_PROXY = new TempleControllerProxyBlock();
        PROTECT_VILLAGER_CONTROLLER = new ChallengeControllerBlock(() -> ModBlocks.PROTECT_VILLAGER_CONTROLLER_TILE_ENTITY);
        TEMPLE_CONTROLLER = new TempleControllerBlock(() -> ModBlocks.TEMPLE_CONTROLLER_TILE_ENTITY);

        RED_CARDBOARD_BOX = new ColoredCardboardBoxBlock(() -> ModBlocks.RED_CARDBOARD_BOX_TILE_ENTITY);
        YELLOW_CARDBOARD_BOX = new ColoredCardboardBoxBlock(() -> ModBlocks.YELLOW_CARDBOARD_BOX_TILE_ENTITY);
        GREEN_CARDBOARD_BOX = new ColoredCardboardBoxBlock(() -> ModBlocks.GREEN_CARDBOARD_BOX_TILE_ENTITY);
        BLUE_CARDBOARD_BOX = new ColoredCardboardBoxBlock(() -> ModBlocks.BLUE_CARDBOARD_BOX_TILE_ENTITY);

        RED_CARDBOARD_BOX_TILE_ENTITY = BlockEntityType.Builder.<CardboardBoxTileEntity>of(ColoredCardboardBoxTileEntity::new, RED_CARDBOARD_BOX).build(null);
        YELLOW_CARDBOARD_BOX_TILE_ENTITY = BlockEntityType.Builder.<CardboardBoxTileEntity>of(ColoredCardboardBoxTileEntity::new, YELLOW_CARDBOARD_BOX).build(null);
        GREEN_CARDBOARD_BOX_TILE_ENTITY = BlockEntityType.Builder.<CardboardBoxTileEntity>of(ColoredCardboardBoxTileEntity::new, GREEN_CARDBOARD_BOX).build(null);
        BLUE_CARDBOARD_BOX_TILE_ENTITY = BlockEntityType.Builder.<CardboardBoxTileEntity>of(ColoredCardboardBoxTileEntity::new, BLUE_CARDBOARD_BOX).build(null);
    }

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        registerBlock(event, VAULT_SALVAGER_BLOCK, WoldsVaults.id("vault_salvager"));
        registerBlock(event, ISKALLIAN_LEAVES_BLOCK, WoldsVaults.id("iskallian_leaves"));
        registerBlock(event, HELLISH_SAND_BLOCK, WoldsVaults.id("hellish_sand"));
        registerBlock(event, DUNGEON_PEDESTAL_BLOCK, WoldsVaults.id("dungeon_pedestal"));
        registerBlock(event, DECO_SCAVENGER_ALTAR_BLOCK, WoldsVaults.id("scavenger_altar"));
        registerBlock(event, DECO_BOSS_RUNE_PILLAR_BLOCK, WoldsVaults.id("rune_pillar"));
        registerBlock(event, DECO_OBELISK_BLOCK, WoldsVaults.id("obelisk"));
        registerBlock(event, DECO_LODESTONE_BLOCK, WoldsVaults.id("lodestone"));
        registerBlock(event, DECO_MONOLITH_BLOCK, WoldsVaults.id("monolith"));
        registerBlock(event, SURVIVAL_MOB_BARRIER, WoldsVaults.id("mob_barrier_red"));
        registerBlock(event, DECO_GRID_GATEWAY_BLOCK, WoldsVaults.id("grid_gateway"));
        registerBlock(event, DECO_IDONA_ALTAR_BLOCK, WoldsVaults.id("idona_altar"));
        registerBlock(event, DECO_TENOS_ALTAR_BLOCK, WoldsVaults.id("tenos_altar"));
        registerBlock(event, DECO_WENDARR_ALTAR_BLOCK, WoldsVaults.id("wendarr_altar"));
        registerBlock(event, DECO_VELARA_ALTAR_BLOCK, WoldsVaults.id("velara_altar"));
        registerBlock(event, VAULT_PALLADIUM_PILE, WoldsVaults.id("vault_palladium"));
        registerBlock(event, VAULT_IRIDIUM_PILE, WoldsVaults.id("vault_iridium"));
        registerBlock(event, BREWING_ALTAR, WoldsVaults.id("brewing_altar"));
        registerBlock(event, VAULT_CRATE_ALCHEMY, WoldsVaults.id("vault_crate_alchemy"));
        registerBlock(event, VAULT_CRATE_BRUTAL_BOSSES, WoldsVaults.id("vault_crate_brutal_bosses"));
        registerBlock(event, VAULT_CRATE_HAUNTED_BRAZIERS, WoldsVaults.id("vault_crate_haunted_braziers"));
        registerBlock(event, VAULT_CRATE_BALLISTIC_BINGO, WoldsVaults.id("vault_crate_ballistic_bingo"));
        registerBlock(event, VAULT_CRATE_UNHINGED_SCAVENGER, WoldsVaults.id("vault_crate_unhinged_scavenger"));
        registerBlock(event, CRYSTALLIZED_STONE, WoldsVaults.id("crystallized_stone"));
        registerBlock(event, CHROMATIC_GOLD_BLOCK, WoldsVaults.id("chromatic_gold_block"));
        registerBlock(event, OMEGA_POG_BLOCK, WoldsVaults.id("omega_pog_block"));
        registerBlock(event, ECHO_POG_BLOCK, WoldsVaults.id("echo_pog_block"));
        registerBlock(event, GEM_POG_BLOCK, WoldsVaults.id("gem_pog_block"));
        registerBlock(event, COMPRESSED_VAULT_DIAMOND_BLOCK, WoldsVaults.id("compressed_vault_diamond_block"));
        registerBlock(event, COMPRESSED_VAULT_ESSENCE, WoldsVaults.id("compressed_vault_essence"));
        registerBlock(event, COMPRESSED_VAULT_ESSENCE_2, WoldsVaults.id("compressed_vault_essence_2"));
        registerBlock(event, COMPRESSED_VAULT_CARBON, WoldsVaults.id("compressed_vault_carbon"));
        registerBlock(event, KNOWLEDGE_STAR_BLOCK, WoldsVaults.id("knowledge_star_block"));
        registerBlock(event, WENDARR_SAND, WoldsVaults.id("wendarr_sand"));
        registerBlock(event, TENOS_SAND, WoldsVaults.id("tenos_sand"));
        registerBlock(event, VELARA_SAND, WoldsVaults.id("velara_sand"));
        registerBlock(event, IDONA_SAND, WoldsVaults.id("idona_sand"));
        registerBlock(event, XL_BACKPACK, WoldsVaults.id("xl_backpack"));
        registerBlock(event, RED_CARDBOARD_BOX, WoldsVaults.id("red_cardboard_box"));
        registerBlock(event, YELLOW_CARDBOARD_BOX, WoldsVaults.id("yellow_cardboard_box"));
        registerBlock(event, GREEN_CARDBOARD_BOX, WoldsVaults.id("green_cardboard_box"));
        registerBlock(event, BLUE_CARDBOARD_BOX, WoldsVaults.id("blue_cardboard_box"));
        registerBlock(event, PROTECT_VILLAGER_CONTROLLER_PROXY, WoldsVaults.id("protect_villager_controller_proxy_block"));
        registerBlock(event, TEMPLE_CONTROLLER_PROXY, WoldsVaults.id("temple_controller_proxy"));
        registerBlock(event, PROTECT_VILLAGER_CONTROLLER, WoldsVaults.id("protect_villager_controller"));
        registerBlock(event, TEMPLE_CONTROLLER, WoldsVaults.id("temple_controller"));

    }
    public static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        registerTileEntity(event, VAULT_SALVAGER_ENTITY, VaultMod.id("vault_salvager_tile_entity"));
        registerTileEntity(event, ISKALLIAN_LEAVES_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("iskallian_leaves_tile_entity"));
        registerTileEntity(event, HELLISH_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("hellish_sand_tile_entity"));
        registerTileEntity(event, WENDARR_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("wendarr_sand_tile_entity"));
        registerTileEntity(event, TENOS_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("tenos_sand_tile_entity"));
        registerTileEntity(event, VELARA_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("velara_sand_tile_entity"));
        registerTileEntity(event, IDONA_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("idona_sand_tile_entity"));
        registerTileEntity(event, DUNGEON_PEDESTAL_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("dungeon_pedestal_tile_entity"));
        registerTileEntity(event, DECO_SCAVENGER_ALTAR_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("scavenger_altar_deco_tile_entity"));
        registerTileEntity(event, DECO_BOSS_RUNE_PILLAR_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("boss_rune_pillar_deco_tile_entity"));
        registerTileEntity(event, DECO_OBELISK_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("obelisk_deco_tile_entity"));
        registerTileEntity(event, DECO_LODESTONE_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("lodestone_deco_tile_entity"));
        registerTileEntity(event, DECO_MONOLITH_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("monolith_deco_tile_entity"));
        registerTileEntity(event, SURVIVAL_MOB_BARRIER_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("mob_barrier_entity"));
        registerTileEntity(event, DECO_GRID_GATEWAY_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("grid_gateway_deco_tile_entity"));
        registerTileEntity(event, DECO_IDONA_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("god_altar.idona_deco_tile_entity"));
        registerTileEntity(event, DECO_TENOS_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("god_altar.tenos_deco_tile_entity"));
        registerTileEntity(event, DECO_WENDARR_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("god_altar.wendarr_deco_tile_entity"));
        registerTileEntity(event, DECO_VELARA_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("god_altar.velara_deco_tile_entity"));
        registerTileEntity(event, BREWING_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE, WoldsVaults.id("brewing_altar_tile_entity"));
        registerTileEntity(event, RED_CARDBOARD_BOX_TILE_ENTITY, WoldsVaults.id("red_cardboard_box_tile_entity"));
        registerTileEntity(event, YELLOW_CARDBOARD_BOX_TILE_ENTITY, WoldsVaults.id("yellow_cardboard_box_tile_entity"));
        registerTileEntity(event, GREEN_CARDBOARD_BOX_TILE_ENTITY, WoldsVaults.id("green_cardboard_box_tile_entity"));
        registerTileEntity(event, BLUE_CARDBOARD_BOX_TILE_ENTITY, WoldsVaults.id("blue_cardboard_box_tile_entity"));
        registerTileEntity(event, PROTECT_VILLAGER_CONTROLLER_TILE_ENTITY, WoldsVaults.id("protect_villager_controller_tile_entity"));
        registerTileEntity(event, TEMPLE_CONTROLLER_TILE_ENTITY, WoldsVaults.id("temple_controller_tile_entity"));
        registerTileEntity(event, PROTECT_VILLAGER_CONTROLLER_PROXY_TILE_ENTITY, WoldsVaults.id("protect_villager_controller_proxy_tile_entity"));
        registerTileEntity(event, TEMPLE_CONTROLLER_PROXY_TILE_ENTITY, WoldsVaults.id("temple_controller_proxy_tile_entity"));
    }

    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        registerBlockItem(event, VAULT_SALVAGER_BLOCK);
        registerBlockItem(event, ISKALLIAN_LEAVES_BLOCK);
        registerBlockItem(event, HELLISH_SAND_BLOCK);
        registerBlockItem(event, DUNGEON_PEDESTAL_BLOCK);
        registerBlockItem(event, DECO_SCAVENGER_ALTAR_BLOCK);
        registerBlockItem(event, DECO_BOSS_RUNE_PILLAR_BLOCK);
        registerBlockItem(event, DECO_OBELISK_BLOCK);
        registerBlockItem(event, DECO_LODESTONE_BLOCK);
        registerBlockItem(event, DECO_MONOLITH_BLOCK);
        registerBlockItem(event, SURVIVAL_MOB_BARRIER);
        registerBlockItem(event, DECO_GRID_GATEWAY_BLOCK);
        registerBlockItem(event, DECO_IDONA_ALTAR_BLOCK);
        registerBlockItem(event, DECO_TENOS_ALTAR_BLOCK);
        registerBlockItem(event, DECO_WENDARR_ALTAR_BLOCK);
        registerBlockItem(event, DECO_VELARA_ALTAR_BLOCK);
        registerBlockItem(event, PROTECT_VILLAGER_CONTROLLER_PROXY, 1);
        registerBlockItem(event, TEMPLE_CONTROLLER_PROXY, 1);
        registerBlockItem(event, PROTECT_VILLAGER_CONTROLLER, 1);
        registerBlockItem(event, TEMPLE_CONTROLLER, 1);
        registerBlockItem(event, CHROMATIC_GOLD_BLOCK);
        registerBlockItem(event, OMEGA_POG_BLOCK);
        registerBlockItem(event, ECHO_POG_BLOCK);
        registerBlockItem(event, GEM_POG_BLOCK);
        registerBlockItem(event, COMPRESSED_VAULT_DIAMOND_BLOCK);
        registerBlockItem(event, COMPRESSED_VAULT_ESSENCE);
        registerBlockItem(event, COMPRESSED_VAULT_ESSENCE_2);
        registerBlockItem(event, COMPRESSED_VAULT_CARBON);
        registerBlockItem(event, KNOWLEDGE_STAR_BLOCK);
        registerBlockItem(event, WENDARR_SAND);
        registerBlockItem(event, TENOS_SAND);
        registerBlockItem(event, VELARA_SAND);
        registerBlockItem(event, IDONA_SAND);
        registerBlockItem(event, VAULT_PALLADIUM_PILE, VAULT_PALLADIUM);
        registerBlockItem(event, VAULT_IRIDIUM_PILE, VAULT_IRIDIUM);
        registerBlockItem(event, BREWING_ALTAR);
        registerBlockItem(event, VAULT_CRATE_ALCHEMY);
        registerBlockItem(event, VAULT_CRATE_BRUTAL_BOSSES);
        registerBlockItem(event, VAULT_CRATE_HAUNTED_BRAZIERS);
        registerBlockItem(event, VAULT_CRATE_BALLISTIC_BINGO);
        registerBlockItem(event, VAULT_CRATE_UNHINGED_SCAVENGER);
        registerBlockItem(event, CRYSTALLIZED_STONE);
        registerBlockItem(event, RED_CARDBOARD_BOX);
        registerBlockItem(event, YELLOW_CARDBOARD_BOX);
        registerBlockItem(event, GREEN_CARDBOARD_BOX);
        registerBlockItem(event, BLUE_CARDBOARD_BOX);

    }

    public static void registerTileEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(DECO_SCAVENGER_ALTAR_ENTITY_BLOCK_ENTITY_TYPE, ScavengerAltarRenderer::new);
        event.registerBlockEntityRenderer(SURVIVAL_MOB_BARRIER_TILE_ENTITY_BLOCK_ENTITY_TYPE, SurvivalMobBarrierRenderer::new);
        event.registerBlockEntityRenderer(DUNGEON_PEDESTAL_TILE_ENTITY_BLOCK_ENTITY_TYPE, DungeonPedestalRenderer::new);
        event.registerBlockEntityRenderer(DECO_GRID_GATEWAY_TILE_ENTITY_BLOCK_ENTITY_TYPE, DecoGridGatewayRenderer::new);
        event.registerBlockEntityRenderer(BREWING_ALTAR_TILE_ENTITY_BLOCK_ENTITY_TYPE, BrewingAltarRenderer::new);
        event.registerBlockEntityRenderer(PROTECT_VILLAGER_CONTROLLER_TILE_ENTITY, ChallengeControllerRenderer::new);
        event.registerBlockEntityRenderer(TEMPLE_CONTROLLER_TILE_ENTITY, TempleControllerRenderer::new);
        event.registerBlockEntityRenderer(PROTECT_VILLAGER_CONTROLLER_PROXY_TILE_ENTITY, ProtectVillagerControllerProxyRenderer::new);
        event.registerBlockEntityRenderer(TEMPLE_CONTROLLER_PROXY_TILE_ENTITY, TempleControllerProxyRenderer::new);
    }



    private static void registerBlock(RegistryEvent.Register<Block> event, Block block, ResourceLocation id) {
        block.setRegistryName(id);
        event.getRegistry().register(block);
    }

    private static BlockEntityType<WendarrSandTileEntity> getWendarrSandTileEntityType() {
        return WENDARR_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    }

    private static BlockEntityType<TenosSandTileEntity> getTenosSandTileEntityType() {
        return TENOS_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    }

    private static BlockEntityType<VelaraSandTileEntity> getVelaraSandTileEntityType() {
        return VELARA_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    }

    private static BlockEntityType<IdonaSandTileEntity> getIdonaSandTileEntityType() {
        return IDONA_SAND_TILE_ENTITY_BLOCK_ENTITY_TYPE;
    }

    public static final BlockEntityType<ProtectVillagerControllerTileEntity> PROTECT_VILLAGER_CONTROLLER_TILE_ENTITY =
            BlockEntityType.Builder.of(ProtectVillagerControllerTileEntity::new, PROTECT_VILLAGER_CONTROLLER).build(null);

    public static final BlockEntityType<ProtectVillagerControllerProxyBlockEntity> PROTECT_VILLAGER_CONTROLLER_PROXY_TILE_ENTITY =
            BlockEntityType.Builder.of(ProtectVillagerControllerProxyBlockEntity::new, PROTECT_VILLAGER_CONTROLLER_PROXY).build(null);


    public static final BlockEntityType<TempleControllerTileEntity> TEMPLE_CONTROLLER_TILE_ENTITY =
            BlockEntityType.Builder.of(TempleControllerTileEntity::new, TEMPLE_CONTROLLER).build(null);

    public static final BlockEntityType<TempleControllerProxyBlockEntity> TEMPLE_CONTROLLER_PROXY_TILE_ENTITY =
            BlockEntityType.Builder.of(TempleControllerProxyBlockEntity::new, TEMPLE_CONTROLLER_PROXY).build(null);

    private static <T extends BlockEntity> void registerTileEntity(RegistryEvent.Register<BlockEntityType<?>> event, BlockEntityType<?> type, ResourceLocation id) {
        type.setRegistryName(id);
        event.getRegistry().register(type);
    }

    private static void registerBlockItemWithEffect(RegistryEvent.Register<Item> event, Block block, int maxStackSize, Consumer<Item.Properties> adjustProperties) {
        Item.Properties properties = (new Item.Properties()).tab(iskallia.vault.init.ModItems.VAULT_MOD_GROUP).stacksTo(maxStackSize);
        adjustProperties.accept(properties);
        BlockItem blockItem = new BlockItem(block, properties) {
            public boolean isFoil(ItemStack stack) {
                return true;
            }
        };
        registerBlockItem(event, block, blockItem);
    }

    private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block) {
        registerBlockItem(event, block, 64);
    }

    private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block, int maxStackSize) {
        registerBlockItem(event, block, maxStackSize, (properties) -> {
        });
    }

    private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block, int maxStackSize, Consumer<Item.Properties> adjustProperties) {
        Item.Properties properties = (new Item.Properties()).tab(iskallia.vault.init.ModItems.VAULT_MOD_GROUP).stacksTo(maxStackSize);
        adjustProperties.accept(properties);
        registerBlockItem(event, block, new BlockItem(block, properties));
    }


    private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block, BlockItem blockItem) {
        blockItem.setRegistryName(block.getRegistryName());
        event.getRegistry().register(blockItem);
    }

    private static void registerTallBlockItem(RegistryEvent.Register<Item> event, Block block) {
        DoubleHighBlockItem tallBlockItem = new DoubleHighBlockItem(block, (new Item.Properties()).tab(iskallia.vault.init.ModItems.VAULT_MOD_GROUP).stacksTo(64));
        tallBlockItem.setRegistryName(block.getRegistryName());
        event.getRegistry().register(tallBlockItem);
    }

    // Merged from the former init.additions class.
public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, WoldsVaults.MOD_ID);


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

    public static final RegistryObject<Block> CRYSTAL_TRADER_CONTROLLER = registerBlock("crystal_trader_controller",
            () -> new CrystalTraderControllerBlock(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(-1, 3.6E8F).noOcclusion().noDrops()),
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
        if(createBlockItem) registerBlockItem(name, toReturn, WoldsVaults.VAULT_ADDITIONS_TAB);
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
