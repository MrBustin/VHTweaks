package io.github.a1qs.vaultadditions.util;

import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.vault.gear.gecko.item.GeckoHandHeldModel;
import io.github.a1qs.vaultadditions.vault.gear.gecko.item.GeckoPlainModel;
import io.github.a1qs.vaultadditions.vault.gear.gecko.item.GeckoShieldModel;
import iskallia.vault.VaultMod;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.model.item.HandHeldModel;
import iskallia.vault.dynamodel.model.item.PlainItemModel;
import iskallia.vault.dynamodel.model.item.shield.ShieldModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.init.ModDynamicModels;
import net.minecraft.resources.ResourceLocation;
import xyz.iwolfking.woldsvaults.models.Battlestaffs;
import xyz.iwolfking.woldsvaults.models.Tridents;

public enum ModelType {
    SWORD(ModDynamicModels.Swords.REGISTRY, HandHeldModel::new, GeckoHandHeldModel::new),
    AXE(ModDynamicModels.Axes.REGISTRY, HandHeldModel::new, GeckoHandHeldModel::new),
    BATTLESTAFF(Battlestaffs.REGISTRY, HandHeldModel::new, GeckoHandHeldModel::new),
    TRIDENT(Tridents.REGISTRY, HandHeldModel::new, GeckoHandHeldModel::new),
    SHIELD(ModDynamicModels.Shields.REGISTRY, ShieldModel::new, GeckoShieldModel::new),
    FOCUS(ModDynamicModels.Focus.REGISTRY, PlainItemModel::new, GeckoPlainModel::new),
    WAND(ModDynamicModels.Wands.REGISTRY, PlainItemModel::new, GeckoPlainModel::new),
    MAGNETS(ModDynamicModels.Magnets.REGISTRY, PlainItemModel::new, GeckoPlainModel::new),
    WENDARR_IDOL("idol", ModDynamicModels.Idols.REGISTRY_WENDARR, PlainItemModel::new, GeckoPlainModel::new),
    IDONA_IDOL("idol", ModDynamicModels.Idols.REGISTRY_IDONA, PlainItemModel::new, GeckoPlainModel::new),
    VELARA_IDOL("idol", ModDynamicModels.Idols.REGISTRY_VELARA, PlainItemModel::new, GeckoPlainModel::new),
    TENOS_IDOL("idol", ModDynamicModels.Idols.REGISTRY_TENOS, PlainItemModel::new, GeckoPlainModel::new);

    private final String type;
    private final DynamicModelRegistry<?> registry;
    private final ModelFactory modelFactory;
    private final GeckoModelFactory geckoModelFactory;

    ModelType(DynamicModelRegistry<?> registry, ModelFactory modelFactory, GeckoModelFactory geckoModelFactory) {
        this.type = name().toLowerCase();
        this.registry = registry;
        this.modelFactory = modelFactory;
        this.geckoModelFactory = geckoModelFactory;
    }

    ModelType(String type, DynamicModelRegistry<?> registry, ModelFactory modelFactory, GeckoModelFactory geckoModelFactory) {
        this.type = type;
        this.registry = registry;
        this.modelFactory = modelFactory;
        this.geckoModelFactory = geckoModelFactory;
    }

    public DynamicModel<?> createModel(String id, String displayName) {
        return modelFactory.create(VaultMod.id("gear/" + type + "/" + id), displayName);
    }

    public DynamicModel<?> createModel(ResourceLocation id, String displayName) {
        return modelFactory.create(id, displayName);
    }

    public DynamicModel<?> createGeckoModel(String id, String displayName, String animationName, float transitionTicks) {
        return geckoModelFactory.create(id, type, displayName, animationName, transitionTicks);
    }

    public DynamicModelRegistry<?> getRegistry() {
        return registry;
    }

    public void register(DynamicModel<?> model) {
        registry.register(forceCast(model));
    }

    public static ModelType getValue(String value) {
        if (value != null) {
            return switch (value) {
                case "sword", "swords" -> SWORD;
                case "axe", "axes" -> AXE;
                case "battlestaff", "battlestaffs" -> BATTLESTAFF;
                case "trident", "tridents" -> TRIDENT;
                case "shield", "shields" -> SHIELD;
                case "focus", "focuses" -> FOCUS;
                case "wand", "wands" -> WAND;
                case "magnet", "magnets" -> MAGNETS;
                case "wendarr", "wendarr_idol" -> WENDARR_IDOL;
                case "idona", "idona_idol" -> IDONA_IDOL;
                case "velara", "velara_idol" -> VELARA_IDOL;
                case "tenos", "tenos_idol" -> TENOS_IDOL;
                default -> null;
            };
        }
        return null;
    }

    @FunctionalInterface
    private interface ModelFactory {
        DynamicModel<?> create(ResourceLocation id, String displayName);
    }

    @FunctionalInterface
    private interface GeckoModelFactory {
        DynamicModel<?> create(String id, String type, String displayName, String animationName, float transitionTicks);
    }

    private static <C> C forceCast(Object obj) {
        try {
            return (C) obj;
        } catch (Exception e) {
            VaultAdditions.LOGGER.error("Failed to cast object " + obj + " to expected type", e);
            throw e;
        }
    }
}
