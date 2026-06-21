package xyz.iwolfking.woldsvaults.init.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.util.WorldHelper;
import xyz.iwolfking.woldsvaults.init.ModBlocks;
import xyz.iwolfking.woldsvaults.init.ModItems;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.blocks.renderers.ColoredVelvetBedRenderer;
import xyz.iwolfking.woldsvaults.util.ModelType;
import xyz.iwolfking.woldsvaults.gear.gecko.armor.GeckoArmorModel;
import xyz.iwolfking.woldsvaults.gear.model.armor.layers.*;
import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.DynamicModelProperties;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.init.ModDynamicModels;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import java.util.Set;

@Mod.EventBusSubscriber(value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModModels {
    public static void setupRenderLayers() {
        ItemBlockRenderTypes.setRenderLayer((Block) ModBlocks.SURVIVAL_MOB_BARRIER, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer((Block) ModBlocks.ISKALLIAN_LEAVES_BLOCK, RenderType.translucent());
    }


    @SubscribeEvent
    public static void registerBlockColorHandles(ColorHandlerEvent.Block event) {
        event.getBlockColors().register((state, blockDisplayReader, pos, tintIndex) -> {
            if(tintIndex < 0 || tintIndex > 1 || pos == null) {
                return -1;
            }
            return WorldHelper.getBlockEntity(blockDisplayReader, pos, BackpackBlockEntity.class).map(te -> tintIndex == 0 ? te.getBackpackWrapper().getMainColor() : te.getBackpackWrapper().getAccentColor()).orElse(getDefaultColor(tintIndex));
        }, ModBlocks.XL_BACKPACK);
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(ColorHandlerEvent.Item event) {
        event.getItemColors().register((backpack, layer) -> {
            if(layer > 1 || !(backpack.getItem() instanceof BackpackItem)) {
                return -1;
            }
            return backpack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance()).map(backpackWrapper -> {
                if(layer == 0) {
                    return backpackWrapper.getMainColor();
                }
                else if(layer == 1) {
                    return backpackWrapper.getAccentColor();
                }
                return -1;
            }).orElse(BackpackWrapper.DEFAULT_CLOTH_COLOR);
        }, ModItems.XL_BACKPACK);
    }

    public static int getDefaultColor(int tintIndex) {
        return tintIndex == 0 ? BackpackWrapper.DEFAULT_CLOTH_COLOR : BackpackWrapper.DEFAULT_BORDER_COLOR;
    }

    // Merged from the former init.additions class.
private static boolean registered = false;
    public static final Set<ArmorModel> HOY_ARMOR = Set.of(Armor.HOY_82.model, Armor.HOY_82_GROGU.model, Armor.DINDJARIN.model, Armor.BOKATAN.model, GeckoArmor.GROGU.model);
    public static final Set<ArmorModel> HOKAGE_ARMOR = Set.of(Armor.HOKAGE_ROBES.model, Armor.HOKAGE_ROBES_MASKLESS.model);
    static {
        register();
    }

    public static void register() {
        if (registered) {
            return;
        }

        registered = true;
        for (Armor armor : Armor.values()) {
            ModDynamicModels.Armor.PIECE_REGISTRY.registerAll(armor.getModel());
        }
        for (GeckoArmor armor : GeckoArmor.values()) {
            ModDynamicModels.Armor.PIECE_REGISTRY.registerAll(armor.getModel());
        }
        for (Item item : Item.values()) {
            item.getType().register(item.model);
        }
        // `madness` is rolled from GearModelRollRaritiesConfig, so it must exist server-side.
        GeckoItem.MADNESS.getType().register(GeckoItem.MADNESS.model);
    }

    @SubscribeEvent @OnlyIn(Dist.CLIENT)
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ColoredVelvetBedRenderer.HEAD_LAYER_LOCATION, ColoredVelvetBedRenderer::createHeadLayer);
        event.registerLayerDefinition(ColoredVelvetBedRenderer.FOOT_LAYER_LOCATION, ColoredVelvetBedRenderer::createFootLayer);
    }

    @SubscribeEvent @OnlyIn(Dist.CLIENT)
    public static void stitchTextures(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            for (DyeColor color : DyeColor.values()) {
                ResourceLocation texture = WoldsVaults.id("entity/bed/velvet_bed_" + color.getName());
                event.addSprite(texture);
            }
        }
    }

    public enum Armor {
        HOY_82("hoy", "Beskar", new HoyArmorLayers()),
        HOY_82_GROGU("hoy_with_grogu", "Beskar & Grogu", new HoyGroguArmorLayers()),
        DINDJARIN("dindjarin", "DinDjarin", new DindjarinArmorLayers()),
        HOKAGE_ROBES("hokage_robes", "Hokage Robes", new HokageRobesArmorLayers()),
        HOKAGE_ROBES_MASKLESS("hokage_robes_maskless", "Hokage Robes Maskless", new HokageRobesMasklessArmorLayers()),
        CELESTIAL("celestial", "Celestial", new CelestialArmorLayers()),
        VIKING("viking", "Viking", new VikingArmorLayers(), true),
        BOKATAN("bokatan", "Bokatan", new BokatanArmorLayers()),
        SPACE_MARINE("spacemarine", "Space Marine", new SpaceMarineArmorLayers());

        private final ArmorModel model;

        Armor(String id, String displayName, ArmorLayers layers) {
            this(id, displayName, layers, true);
        }

        Armor(String id, String displayName, ArmorLayers layers, boolean discoverOnRoll) {
            this(id, displayName, layers, discoverOnRoll, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
        }

        Armor(String id, String displayName, ArmorLayers layers, boolean discoverOnRoll, EquipmentSlot... slots) {
            this.model = new ArmorModel(VaultMod.id("gear/armor/" + id), displayName);
            this.model.properties(new DynamicModelProperties().allowTransmogrification());
            this.model.usingLayers(layers);
            if (discoverOnRoll) {
                this.model.getModelProperties().discoverOnRoll();
            }
            for (EquipmentSlot slot : slots) {
                this.model.addSlot(slot);
            }
        }

        public ArmorModel getModel() {
            return this.model;
        }
    }

    public enum GeckoArmor {
        GROGU("grogu", "Beskar & Grogu+Ball", "grogu", 20, false),
        ELDRITCH("eldritch", "Eldritch", "tenticle1", 20, false);
        private final GeckoArmorModel model;

        GeckoArmor(String id, String displayName, String animationName, int transitionTicks) {
            this(id, displayName, animationName, transitionTicks, true);
        }

        GeckoArmor(String id, String displayName, String animationName, int transitionTicks, boolean discoverOnRoll) {
            this(id, displayName, animationName, transitionTicks, discoverOnRoll, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
        }

        GeckoArmor(String id, String displayName, String animationName, int transitionTicks, boolean discoverOnRoll, EquipmentSlot... slots) {
            this.model = new GeckoArmorModel(id, displayName, animationName, transitionTicks);
            this.model.properties(new DynamicModelProperties().allowTransmogrification());
            this.model.usingLayers(new DummyArmorLayers());
            if (discoverOnRoll) {
                this.model.getModelProperties().discoverOnRoll();
            }
            for (EquipmentSlot slot : slots) {
                this.model.addSlot(slot);
            }
        }

        public GeckoArmorModel getModel() {
            return this.model;
        }
    }

    public enum Item {
        DARKSABER("darksaber", "The Darksaber", ModelType.BATTLESTAFF),
        SIDEARM("sidearm", "Sidearm", ModelType.WAND),
        RELIC_SHIELD("relicshield", "Relic Shield", ModelType.SHIELD),
        CHAIN_SWORD("chain_sword", "Chain-Sword", ModelType.SWORD),
        DARKSABER2("darksaber2", "The Darksaber", ModelType.BATTLESTAFF),
        ELDRITCH_STAFF("eldritch_staff", "Eldritch Staff", ModelType.BATTLESTAFF),
        ELDRITCH_SHIELD("eldritch_shield", "Eldritch Shield", ModelType.WAND),
        EXECUTIONER("executioner", "Kubikiribōchō", ModelType.SWORD);

        private final DynamicModel<?> model;
        private final ModelType type;

        Item(String id, String displayName, ModelType type) {
            this(id, displayName, type, true);
        }

        Item(String id, String displayName, ModelType type, boolean discoverOnRoll) {
            this.model = type.createModel(id, displayName);
            this.model.properties(new DynamicModelProperties().allowTransmogrification());
            if (discoverOnRoll) {
                this.model.getModelProperties().discoverOnRoll();
            }
            this.type = type;
        }

        public DynamicModel<?> getModel() {
            return this.model;
        }

        public ModelType getType() {
            return this.type;
        }
    }

    public enum GeckoItem {
        MADNESS("madness", "Madness Focus", "balls", 20, ModelType.FOCUS),
        DARKSABER2("darksaber2", "The Darksaber 2", "blade", 20, ModelType.BATTLESTAFF);
        private final DynamicModel<?> model;
        private final ModelType type;

        GeckoItem(String id, String displayName, String animationName, float transitionTicks, ModelType type) {
            this(id, displayName, animationName, transitionTicks, type, true);
        }

        GeckoItem(String id, String displayName, String animationName, float transitionTicks, ModelType type, boolean discoverOnRoll) {
            this.model = type.createGeckoModel(id, displayName, animationName, transitionTicks);
            this.model.properties(new DynamicModelProperties().allowTransmogrification());
            if (discoverOnRoll) {
                this.model.getModelProperties().discoverOnRoll();
            }
            this.type = type;
        }

        public DynamicModel<?> getModel() {
            return this.model;
        }

        public ModelType getType() {
            return this.type;
        }
    }
}
