package xyz.iwolfking.woldsvaults.mixin.the_vault;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.init.ModConfigs;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.iwolfking.woldsvaults.api.data.gear.UnusualModifiers;
import xyz.iwolfking.woldsvaults.mixin.the_vault.accessors.VaultGearTierConfigAccessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


@Mixin(value = ModConfigs.class, remap = false)
public class MixinModConfigs {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Inject(method = "register", at = @At("HEAD"), remap = false)
    private static void beforeVaultRegister(CallbackInfo ci) {
        try {
            Path configDir = FMLPaths.GAMEDIR.get().resolve("config").resolve("the_vault");
            Files.createDirectories(configDir);
            mergeDefaultEntityGroups(configDir.resolve("entity_groups.json"));
            repairBallisticBingoConfig(configDir.resolve("ballistic_bingo.json"));
            mergeObjectiveSealsIntoVaultCrystal(configDir.resolve("vault_crystal.json"));
        } catch (IOException e) {
            System.out.println("[WoldsVaults] Failed to prepare default Vault config files: " + e.getMessage());
        }
    }

    private static void mergeObjectiveSealsIntoVaultCrystal(Path vaultCrystalFile) throws IOException {
        if (!Files.exists(vaultCrystalFile)) {
            return;
        }

        JsonObject bundledSeals;
        try (InputStream stream = MixinModConfigs.class.getResourceAsStream("/vhapi_configs/objective_seals.json")) {
            if (stream == null) {
                return;
            }
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
                JsonElement seals = root.get("SEALS");
                if (seals == null || !seals.isJsonObject()) {
                    return;
                }
                bundledSeals = seals.getAsJsonObject();
            }
        }

        JsonObject targetRoot;
        try (Reader reader = Files.newBufferedReader(vaultCrystalFile, StandardCharsets.UTF_8)) {
            JsonElement parsed = new JsonParser().parse(reader);
            if (parsed == null || !parsed.isJsonObject()) {
                return;
            }
            targetRoot = parsed.getAsJsonObject();
        } catch (RuntimeException e) {
            return;
        }

        JsonObject targetSeals = getOrCreateObject(targetRoot, "SEALS");
        boolean changed = false;

        for (Map.Entry<String, JsonElement> entry : bundledSeals.entrySet()) {
            if (!targetSeals.has(entry.getKey())) {
                targetSeals.add(entry.getKey(), entry.getValue());
                changed = true;
            }
        }

        if (changed) {
            try (Writer writer = Files.newBufferedWriter(vaultCrystalFile, StandardCharsets.UTF_8)) {
                GSON.toJson(targetRoot, writer);
            }
        }
    }

    private static void repairBallisticBingoConfig(Path ballisticBingoFile) throws IOException {
        if (!Files.exists(ballisticBingoFile) || hasSerializedRuntimeTimedCounters(ballisticBingoFile)) {
            try (InputStream stream = MixinModConfigs.class.getResourceAsStream("/default_configs/ballistic_bingo.json")) {
                if (stream == null) {
                    return;
                }

                Files.copy(stream, ballisticBingoFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static boolean hasSerializedRuntimeTimedCounters(Path ballisticBingoFile) throws IOException {
        try (Reader reader = Files.newBufferedReader(ballisticBingoFile, StandardCharsets.UTF_8)) {
            JsonElement parsed = new JsonParser().parse(reader);
            return hasSerializedRuntimeTimedCounters(parsed);
        } catch (RuntimeException e) {
            return true;
        }
    }

    private static boolean hasSerializedRuntimeTimedCounters(JsonElement element) {
        if (element == null || element.isJsonNull() || element.isJsonPrimitive()) {
            return false;
        }

        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (hasSerializedRuntimeTimedCounters(child)) {
                    return true;
                }
            }

            return false;
        }

        JsonObject object = element.getAsJsonObject();
        JsonElement type = object.get("type");
        if (type != null
                && type.isJsonPrimitive()
                && "sliding_timed_target".equals(type.getAsString())
                && object.has("frames")) {
            JsonElement window = object.get("window");
            return window == null || window.isJsonPrimitive();
        }

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (hasSerializedRuntimeTimedCounters(entry.getValue())) {
                return true;
            }
        }

        return false;
    }

    private static void mergeDefaultEntityGroups(Path entityGroupsFile) throws IOException {
        JsonObject bundledRoot;
        try (InputStream stream = MixinModConfigs.class.getResourceAsStream("/default_configs/entity_groups.json")) {
            if (stream == null) {
                return;
            }

            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                bundledRoot = new JsonParser().parse(reader).getAsJsonObject();
            }
        }

        JsonObject targetRoot = new JsonObject();
        if (Files.exists(entityGroupsFile)) {
            try (Reader reader = Files.newBufferedReader(entityGroupsFile, StandardCharsets.UTF_8)) {
                JsonElement parsed = new JsonParser().parse(reader);
                if (parsed != null && parsed.isJsonObject()) {
                    targetRoot = parsed.getAsJsonObject();
                }
            }
        }

        JsonObject bundledGroups = getOrCreateObject(bundledRoot, "groups");
        JsonObject targetGroups = getOrCreateObject(targetRoot, "groups");
        boolean changed = false;

        for (Map.Entry<String, JsonElement> entry : bundledGroups.entrySet()) {
            if (!entry.getValue().isJsonArray()) {
                continue;
            }

            JsonArray merged = new JsonArray();
            Set<String> values = new LinkedHashSet<>();
            JsonElement current = targetGroups.get(entry.getKey());
            if (current != null && current.isJsonArray()) {
                addAll(values, current.getAsJsonArray());
            }

            int before = values.size();
            addAll(values, entry.getValue().getAsJsonArray());
            for (String value : values) {
                merged.add(value);
            }

            if (current == null || !current.equals(merged) || values.size() != before) {
                targetGroups.add(entry.getKey(), merged);
                changed = true;
            }
        }

        if (!Files.exists(entityGroupsFile) || changed) {
            try (Writer writer = Files.newBufferedWriter(entityGroupsFile, StandardCharsets.UTF_8)) {
                GSON.toJson(targetRoot, writer);
            }
        }
    }

    private static JsonObject getOrCreateObject(JsonObject root, String key) {
        JsonElement element = root.get(key);
        if (element != null && element.isJsonObject()) {
            return element.getAsJsonObject();
        }

        JsonObject object = new JsonObject();
        root.add(key, object);
        return object;
    }

    private static void addAll(Set<String> target, JsonArray source) {
        for (JsonElement element : source) {
            if (element.isJsonPrimitive()) {
                target.add(element.getAsString());
            }
        }
    }

    @Inject(method = "register", at = @At("TAIL"), remap = false)
    private static void onReloadConfigs(CallbackInfo ci) {
        xyz.iwolfking.woldsvaults.init.ModConfigs.register();

        //Initialize unusual modifier values
        for(ResourceLocation config : ModConfigs.VAULT_GEAR_CONFIG.keySet()) {
            if(UnusualModifiers.UNUSUAL_MODIFIERS_MAP_PREFIX.containsKey(config)) {
                ((VaultGearTierConfigAccessor)ModConfigs.VAULT_GEAR_CONFIG.get(config)).getModifierGroup().put(VaultGearTierConfig.ModifierAffixTagGroup.valueOf("UNUSUAL_PREFIX"), UnusualModifiers.UNUSUAL_MODIFIERS_MAP_PREFIX.get(config));
            }
        }

        for(ResourceLocation config : ModConfigs.VAULT_GEAR_CONFIG.keySet()) {
            if(UnusualModifiers.UNUSUAL_MODIFIERS_MAP_SUFFIX.containsKey(config)) {
                ((VaultGearTierConfigAccessor)ModConfigs.VAULT_GEAR_CONFIG.get(config)).getModifierGroup().put(VaultGearTierConfig.ModifierAffixTagGroup.valueOf("UNUSUAL_SUFFIX"), UnusualModifiers.UNUSUAL_MODIFIERS_MAP_SUFFIX.get(config));
            }
        }
    }
}
