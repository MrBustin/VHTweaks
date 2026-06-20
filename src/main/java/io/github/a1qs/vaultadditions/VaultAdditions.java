package io.github.a1qs.vaultadditions;

import com.mojang.logging.LogUtils;
import io.github.a1qs.vaultadditions.block.blockentity.render.*;
import io.github.a1qs.vaultadditions.config.ServerConfigs;
import io.github.a1qs.vaultadditions.init.*;
import io.github.a1qs.vaultadditions.vault.core.vault.objective.InfiniteRaidObjective;
import io.github.a1qs.vaultadditions.vault.gear.gecko.armor.VaultGeckoArmorRenderer;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.item.gear.VaultArmorItem;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mod(VaultAdditions.MOD_ID)
public class VaultAdditions {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "vaultadditions";
    public static final CreativeModeTab VAULT_ADDITIONS_TAB = new ModCreativeTab(MOD_ID);

    public VaultAdditions() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(eventBus);
        ModBlocks.BLOCKS.register(eventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(eventBus);
        ModParticles.PARTICLE_TYPES.register(eventBus);
        ModSounds.SOUNDS.register(eventBus);

        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::registerRenderers);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC, "vaultadditions-server.toml");

        MinecraftForge.EVENT_BUS.register(this);

        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER && !ModList.get().isLoaded("worldborderfixer")) {
            LOGGER.error("Worldborderfixer is not installed. Please install 'Multi World Borders Unofficial'!");
        }
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        ModModels.register();
        ModNetwork.initialize();
        VaultRegistry.OBJECTIVE.add(InfiniteRaidObjective.KEY);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
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
        LOGGER.info("Registered VaultAdditions Block Entity Renderers");
    }

    @SubscribeEvent
    public void registerRenderers(final EntityRenderersEvent.AddLayers event) {
        GeoArmorRenderer.registerArmorRenderer(VaultArmorItem.class, VaultGeckoArmorRenderer::new);
        LOGGER.info("Registered VaultAdditions Armor Renderers");
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
