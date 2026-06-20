package io.github.a1qs.vaultadditions.config.vault;

import com.google.gson.annotations.Expose;
import com.mojang.datafixers.util.Pair;
import io.github.a1qs.vaultadditions.VaultAdditions;
import io.github.a1qs.vaultadditions.init.ModModels;
import iskallia.vault.config.Config;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.init.ModDynamicModels;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TransmogUnlocksConfig extends Config {
    @Expose
    public Map<String, List<String>> unlocks = new HashMap<>();

    public Map<UUID, List<DynamicModel<?>>> transmogUnlocks = new HashMap<>();

    public List<DynamicModel<?>> getUnlocks(Player player) {
        return transmogUnlocks.get(player.getUUID());
    }

    @Override
    protected void onLoad(@Nullable Config oldConfigInstance) {
        for (Map.Entry<String, List<String>> entry : unlocks.entrySet()) {
            var model = ModDynamicModels.REGISTRIES.getModelAndAssociatedItem(ResourceLocation.tryParse(entry.getKey())).map(Pair::getFirst).orElse(null);
            if (model != null) {
                for (String playerId : entry.getValue()) {
                    try {
                        UUID uuid = UUID.fromString(playerId);
                        DynamicModel<?> finalModel = model;
                        transmogUnlocks.compute(uuid, (id, models) -> {
                            if (models == null) {
                                models = new ArrayList<>();
                            }
                            models.add(finalModel);
                            return models;
                        });
                    } catch (Exception e) {
                        VaultAdditions.LOGGER.error("[Transmog Unlocks Config] Invalid uuid {} under model {}, skipping", playerId, entry.getKey());
                    }
                }
                continue;
            }

            UUID uuid;
            try {
                uuid = UUID.fromString(entry.getKey());
            } catch (Exception e) {
                VaultAdditions.LOGGER.error("[Transmog Unlocks Config] Invalid key {}, expected UUID, or Model Id, skipping", entry.getKey());
                continue;
            }

            List<DynamicModel<?>> transmogs = new ArrayList<>();
            for (String modelId : entry.getValue()) {
                model = ModDynamicModels.REGISTRIES.getModelAndAssociatedItem(ResourceLocation.tryParse(modelId)).map(Pair::getFirst).orElse(null);
                if (model == null) {
                    VaultAdditions.LOGGER.warn("[Transmog Unlocks Config] Invalid transmog model {} under uuid {}, skipping", modelId, entry.getKey());
                    continue;
                }
                transmogs.add(model);
            }

            transmogUnlocks.put(uuid, transmogs);
        }
    }

    @Override
    protected void reset() {
        // Hoy Unlocks
        unlocks.put("5b61cfde-84b8-4b12-b375-26fe25be0443", List.of(
                ModModels.Armor.HOY_82.getModel().getId().toString(),
                ModModels.Armor.HOY_82_GROGU.getModel().getId().toString(),
                ModModels.Armor.DINDJARIN.getModel().getId().toString(),
                ModModels.GeckoArmor.GROGU.getModel().getId().toString(),
                ModModels.Item.DARKSABER.getModel().getId().toString()
//                ModModels.GeckoItem.DARKSABER2.getModel().getId().toString()
        ));
        // Tiger Unlocks
        unlocks.put("8561dac7-e879-4d98-a92f-c379614eaa4e", List.of(
                ModModels.Armor.HOKAGE_ROBES.getModel().getId().toString(),
                ModModels.Armor.HOKAGE_ROBES_MASKLESS.getModel().getId().toString()
        ));
    }

    @Override
    public String getName() {
        return "vaultadditions_transmog_unlocks";
    }
}
