package xyz.iwolfking.woldsvaults;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import xyz.iwolfking.vhapi.api.events.VHAPIProcessorsEvent;
import xyz.iwolfking.vhapi.api.registry.gear.CustomVaultGearRegistryEntry;
import xyz.iwolfking.vhapi.api.registry.objective.CustomObjectiveRegistryEntry;
import xyz.iwolfking.woldsvaults.configs.core.WoldsVaultsConfig;
import xyz.iwolfking.woldsvaults.core.AddonLoader;
import xyz.iwolfking.woldsvaults.events.LivingEntityEvents;
import xyz.iwolfking.woldsvaults.events.SetupEvents;
import xyz.iwolfking.woldsvaults.init.ModCustomVaultGearEntries;
import xyz.iwolfking.woldsvaults.init.ModCustomVaultObjectiveEntries;
import xyz.iwolfking.woldsvaults.init.ModCrystalObjectives;
import xyz.iwolfking.woldsvaults.init.ModGameRules;
import xyz.iwolfking.woldsvaults.init.ModVaultFilterAttributes;
import xyz.iwolfking.woldsvaults.models.AdditionalModels;
import xyz.iwolfking.woldsvaults.network.PacketHandler;
import xyz.iwolfking.woldsvaults.objectives.data.BrutalBossesRegistry;
import xyz.iwolfking.woldsvaults.objectives.data.EnchantedEventsRegistry;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("woldsvaults")
public class WoldsVaults {

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "woldsvaults";

    public WoldsVaults() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WoldsVaultsConfig.COMMON_SPEC, "woldsvaults-common.toml");
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);


        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addGenericListener(CustomObjectiveRegistryEntry.class, ModCustomVaultObjectiveEntries::registerCustomObjectives);
        modEventBus.addGenericListener(CustomVaultGearRegistryEntry.class, ModCustomVaultGearEntries::registerGearEntries);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AddonLoader::onAddPackFinders);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, SetupEvents::registerCustomModelRolls);
        MinecraftForge.EVENT_BUS.addListener(this::onVHAPIProcessorEnd);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        ModCrystalObjectives.init();
        new AdditionalModels();
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.init();
        ModGameRules.initialize();
        ModVaultFilterAttributes.initAttributes();

        LivingEntityEvents.init();
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
