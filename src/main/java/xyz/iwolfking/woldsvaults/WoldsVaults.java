package xyz.iwolfking.woldsvaults;

import com.mojang.logging.LogUtils;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.item.gear.VaultArmorItem;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import xyz.iwolfking.vhapi.api.events.VHAPIProcessorsEvent;
import xyz.iwolfking.vhapi.api.registry.gear.CustomVaultGearRegistryEntry;
import xyz.iwolfking.vhapi.api.registry.objective.CustomObjectiveRegistryEntry;
import xyz.iwolfking.woldsvaults.blocks.renderers.ColoredVelvetBedRenderer;
import xyz.iwolfking.woldsvaults.blocks.renderers.EventBlockRenderer;
import xyz.iwolfking.woldsvaults.blocks.renderers.GlobeExpanderEntityRenderer;
import xyz.iwolfking.woldsvaults.blocks.renderers.LootStatueBlockRenderer;
import xyz.iwolfking.woldsvaults.blocks.renderers.PlayerTraderBlockRenderer;
import xyz.iwolfking.woldsvaults.blocks.renderers.RaidPlaqueRenderer;
import xyz.iwolfking.woldsvaults.blocks.renderers.StatueCauldronRenderer;
import xyz.iwolfking.woldsvaults.configs.ServerConfigs;
import xyz.iwolfking.woldsvaults.configs.core.WoldsVaultsConfig;
import xyz.iwolfking.woldsvaults.core.AddonLoader;
import xyz.iwolfking.woldsvaults.api.util.DelayedExecutionHelper;
import xyz.iwolfking.woldsvaults.client.overworld.OverworldVaultCompassHandler;
import xyz.iwolfking.woldsvaults.client.overworld.OverworldVaultKeybinds;
import xyz.iwolfking.woldsvaults.client.overworld.gui.OverworldVaultGuiUpdateManager;
import xyz.iwolfking.woldsvaults.events.overworld.DimensionChangeEvent;
import xyz.iwolfking.woldsvaults.events.LivingEntityEvents;
import xyz.iwolfking.woldsvaults.events.overworld.OnPlayerLogin;
import xyz.iwolfking.woldsvaults.events.overworld.OverworldVaultEventHandler;
import xyz.iwolfking.woldsvaults.events.overworld.ServerTickEvent;
import xyz.iwolfking.woldsvaults.events.SetupEvents;
import xyz.iwolfking.woldsvaults.events.overworld.StructureTrackingEventHandler;
import xyz.iwolfking.woldsvaults.events.overworld.VaultLifecycleEventHandler;
import xyz.iwolfking.woldsvaults.init.ModCustomVaultGearEntries;
import xyz.iwolfking.woldsvaults.init.ModCustomVaultObjectiveEntries;
import xyz.iwolfking.woldsvaults.init.ModCrystalObjectives;
import xyz.iwolfking.woldsvaults.init.ModGameRules;
import xyz.iwolfking.woldsvaults.init.ModVaultFilterAttributes;
import xyz.iwolfking.woldsvaults.init.additions.ModBlockEntities;
import xyz.iwolfking.woldsvaults.init.ModBlocks;
import xyz.iwolfking.woldsvaults.init.additions.ModCreativeTab;
import xyz.iwolfking.woldsvaults.init.ModItems;
import xyz.iwolfking.woldsvaults.init.additions.ModKeybinds;
import xyz.iwolfking.woldsvaults.init.client.ModModels;
import xyz.iwolfking.woldsvaults.init.additions.ModNetwork;
import xyz.iwolfking.woldsvaults.init.ModParticles;
import xyz.iwolfking.woldsvaults.init.client.ModScreens;
import xyz.iwolfking.woldsvaults.init.ModSounds;
import xyz.iwolfking.woldsvaults.gear.gecko.armor.VaultGeckoArmorRenderer;
import xyz.iwolfking.woldsvaults.init.overworld.OverworldVaultSounds;
import xyz.iwolfking.woldsvaults.models.AdditionalModels;
import xyz.iwolfking.woldsvaults.network.PacketHandler;
import xyz.iwolfking.woldsvaults.network.overworld.OverworldVaultNetwork;
import xyz.iwolfking.woldsvaults.objectives.InfiniteRaidObjective;
import xyz.iwolfking.woldsvaults.objectives.data.BrutalBossesRegistry;
import xyz.iwolfking.woldsvaults.objectives.data.EnchantedEventsRegistry;
import xyz.iwolfking.woldsvaults.world.overworld.structure.OverworldVaultStructures;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("woldsvaults")
public class WoldsVaults {

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "woldsvaults";
    public static final String MOD_ID = MODID;
    public static final CreativeModeTab VAULT_ADDITIONS_TAB = new ModCreativeTab(MODID);

    public WoldsVaults() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WoldsVaultsConfig.COMMON_SPEC, "woldsvaults-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC, "woldsvaults-server.toml");
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);


        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);

        modEventBus.addGenericListener(CustomObjectiveRegistryEntry.class, ModCustomVaultObjectiveEntries::registerCustomObjectives);
        modEventBus.addGenericListener(CustomVaultGearRegistryEntry.class, ModCustomVaultGearEntries::registerGearEntries);
        modEventBus.addListener(this::additionsCommonSetup);
        modEventBus.addListener(this::additionsClientSetup);
        modEventBus.addListener(this::overworldVaultCommonSetup);
        modEventBus.addListener(this::overworldVaultClientSetup);
        modEventBus.addListener(this::registerRenderers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AddonLoader::onAddPackFinders);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, SetupEvents::registerCustomModelRolls);
        MinecraftForge.EVENT_BUS.addListener(this::onVHAPIProcessorEnd);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(StructureTrackingEventHandler.class);
        MinecraftForge.EVENT_BUS.register(OverworldVaultEventHandler.class);
        MinecraftForge.EVENT_BUS.addListener(ServerTickEvent::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(OverworldVaultGuiUpdateManager::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(DimensionChangeEvent::onDimensionChange);
        MinecraftForge.EVENT_BUS.addListener(OnPlayerLogin::onPlayerLoginEvent);
        ModCrystalObjectives.init();
        OverworldVaultSounds.register(modEventBus);
        OverworldVaultStructures.register(modEventBus);
        new AdditionalModels();

        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER && !ModList.get().isLoaded("worldborderfixer")) {
            LOGGER.error("Worldborderfixer is not installed. Please install 'Multi World Borders Unofficial'!");
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.init();
        ModGameRules.initialize();
        ModVaultFilterAttributes.initAttributes();

        LivingEntityEvents.init();
        DelayedExecutionHelper.init();
    }

    private void additionsCommonSetup(final FMLCommonSetupEvent event) {
        ModModels.register();
        ModNetwork.initialize();
        VaultRegistry.OBJECTIVE.add(InfiniteRaidObjective.KEY);
    }

    private void additionsClientSetup(final FMLClientSetupEvent event) {
        ModScreens.register();
        ModKeybinds.register();
        BlockEntityRenderers.register(ModBlockEntities.EVENT_BLOCK_ENTITY.get(), EventBlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.GLOBE_EXPANDER_ENTITY.get(), GlobeExpanderEntityRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.LOOT_STATUE_BLOCK_ENTITY.get(), LootStatueBlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.STATUE_CAULDRON_BLOCK_ENTITY.get(), StatueCauldronRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.PLAYER_TRADER_BLOCK_ENTITY.get(), PlayerTraderBlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.COLORED_VELVET_BED_BLOCK_ENTITY.get(), ColoredVelvetBedRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.RAID_PLAQUE_BLOCK_ENTITY.get(), RaidPlaqueRenderer::new);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ENCHANTED_FIRE.get(), RenderType.cutout());
        LOGGER.info("Registered Wold's Vaults block entity renderers");
    }

    private void overworldVaultCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            OverworldVaultNetwork.register();
            VaultLifecycleEventHandler.init();
        });
    }

    private void overworldVaultClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            OverworldVaultCompassHandler.init();
            OverworldVaultKeybinds.register();
        });
    }

    @SubscribeEvent
    public void registerRenderers(final EntityRenderersEvent.AddLayers event) {
        GeoArmorRenderer.registerArmorRenderer(VaultArmorItem.class, VaultGeckoArmorRenderer::new);
        LOGGER.info("Registered Wold's Vaults armor renderers");
    }

    private void onVHAPIProcessorEnd(VHAPIProcessorsEvent.End event) {
        SetupEvents.addManualConfigs();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        EnchantedEventsRegistry.addEvents();
        BrutalBossesRegistry.init();
    }


    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static String sId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path).toString();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = "woldsvaults")
    public static class Client {
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void textureStitch(TextureStitchEvent.Pre event) {
            if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
                event.addSprite(ResourceLocation.fromNamespaceAndPath(MODID, "gui/bingo/shopping"));
                event.addSprite(ResourceLocation.fromNamespaceAndPath(MODID, "gui/bingo/vindicator"));
            }
        }
    }
}
