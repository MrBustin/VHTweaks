package io.github.a1qs.vaultadditions.data;

import io.github.a1qs.vaultadditions.config.ServerConfigs;
import io.github.a1qs.vaultadditions.util.MiscUtil;
import iskallia.vault.init.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PowerCrystalData extends SavedData {
    protected static final String DATA_NAME = "vaultadditions_PowerCrystalData";
    private final Map<UUID, Integer> playerContributedCrystals = new HashMap<>();

    public static PowerCrystalData load(CompoundTag nbt) {
        PowerCrystalData data = new PowerCrystalData();
        ListTag playersList = nbt.getList("Players", Tag.TAG_COMPOUND);
        for (int i = 0; i < playersList.size(); i++) {
            CompoundTag playerTag = playersList.getCompound(i);
            UUID playerId = UUID.fromString(playerTag.getString("UUID"));
            int powerValue = playerTag.getInt("ContributedCrystals");
            data.playerContributedCrystals.put(playerId, powerValue);
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        ListTag playersList = new ListTag();
        for (Map.Entry<UUID, Integer> entry : playerContributedCrystals.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putString("UUID", entry.getKey().toString());
            playerTag.putInt("ContributedCrystals", entry.getValue());
            playersList.add(playerTag);
        }
        nbt.put("Players", playersList);
        return nbt;
    }

    public void addContribution(ServerPlayer player, int crystals) {
        setContributed(player, getPlayerContributedCrystals(player.getUUID()) + crystals);
    }

    public void setContributed(ServerPlayer player, int crystals) {
        setContributed(player.getUUID(), crystals);

        // Update the player size
        if (ServerConfigs.GROW_PLAYER_ON_GEMSTONE_SUBMIT.get()) {
            AttributeInstance attribute = player.getAttribute(ModAttributes.SIZE_SCALE);
            if (attribute != null) {
                double growthAmount = crystals * ServerConfigs.GROW_PLAYER_AMOUNT.get();
                growthAmount = Math.min(growthAmount, ServerConfigs.GROW_PLAYER_CAP.get());
                attribute.removeModifier(MiscUtil.sizeScaleModifierUUID);
                if (growthAmount != 0) {
                    attribute.addPermanentModifier(new AttributeModifier(MiscUtil.sizeScaleModifierUUID, "PowerCrystalSizeScale", growthAmount, AttributeModifier.Operation.ADDITION));
                }
            }
        }
    }

    public void setContributed(UUID playerId, int contributedGemstones) {
        playerContributedCrystals.put(playerId, contributedGemstones);
        this.setDirty();
    }

    public int getPlayerContributedCrystals(UUID playerId) {
        return playerContributedCrystals.getOrDefault(playerId, 0);
    }

    public Map<UUID, Integer> getPlayerContributionsMap() {
        return playerContributedCrystals;
    }

    public void resetContributions(ServerPlayer player) {
        setContributed(player, 0);
        setDirty();
    }

    public int getTotalContributedCrystals() {
        return playerContributedCrystals.values().stream().mapToInt(Integer::intValue).sum();
    }

    public static PowerCrystalData getServer() {
        return get(ServerLifecycleHooks.getCurrentServer());
    }

    public static PowerCrystalData get(ServerLevel level) {
        return get(level.getServer());
    }

    public static PowerCrystalData get(MinecraftServer srv) {
        return srv.overworld().getDataStorage().computeIfAbsent(PowerCrystalData::load, PowerCrystalData::new, DATA_NAME);
    }
}

