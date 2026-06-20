package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.annotations.Expose;
import com.mojang.datafixers.util.Pair;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.init.ModModels;
import iskallia.vault.config.Config;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.init.ModDynamicModels;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransmogRoleUnlocksConfig extends Config {
    @Expose
    public Map<String, List<String>> unlocks = new HashMap<>();

    public Map<String, List<DynamicModel<?>>> transmogUnlocks = new HashMap<>();

    public List<DynamicModel<?>> getUnlocks(Collection<String> roleNames) {
        LinkedHashSet<DynamicModel<?>> unlocks = new LinkedHashSet<>();
        for (String roleName : roleNames) {
            List<DynamicModel<?>> models = transmogUnlocks.get(normalizeRole(roleName));
            if (models != null) {
                unlocks.addAll(models);
            }
        }
        return new ArrayList<>(unlocks);
    }

    @Override
    protected void onLoad(@Nullable Config oldConfigInstance) {
        this.transmogUnlocks.clear();

        for (Map.Entry<String, List<String>> entry : this.unlocks.entrySet()) {
            String roleName = normalizeRole(entry.getKey());
            if (roleName.isBlank()) {
                VaultAdditions.LOGGER.error("[Transmog Role Unlocks Config] Invalid blank LuckPerms role key, skipping");
                continue;
            }

            List<DynamicModel<?>> models = new ArrayList<>();
            for (String modelId : entry.getValue()) {
                DynamicModel<?> model = ModDynamicModels.REGISTRIES.getModelAndAssociatedItem(ResourceLocation.tryParse(modelId))
                        .map(Pair::getFirst)
                        .orElse(null);
                if (model == null) {
                    VaultAdditions.LOGGER.warn("[Transmog Role Unlocks Config] Invalid transmog model {} under role {}, skipping", modelId, entry.getKey());
                    continue;
                }
                models.add(model);
            }

            if (!models.isEmpty()) {
                this.transmogUnlocks.put(roleName, models);
            }
        }
    }

    @Override
    protected void reset() {
        unlocks.put("hoy", ModModels.HOY_ARMOR.stream()
                .map(model -> model.getId().toString())
                .toList());

        unlocks.put("tiger", ModModels.HOKAGE_ARMOR.stream()
                .map(model -> model.getId().toString())
                .toList());
    }

    @Override
    public String getName() {
        return "vaultadditions_transmog_role_unlocks";
    }

    private static String normalizeRole(String roleName) {
        return roleName == null ? "" : roleName.trim().toLowerCase(Locale.ROOT);
    }
}
