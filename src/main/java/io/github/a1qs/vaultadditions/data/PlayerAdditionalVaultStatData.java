package io.github.a1qs.vaultadditions.data;

import io.github.a1qs.vaultadditions.vault.PlayerAdditionalVaultStats;
import io.github.a1qs.vaultadditions.vault.menu.PowerTree;
import iskallia.vault.skill.base.GroupedSkill;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAdditionalVaultStatData extends SavedData {
    private static final String DATA_NAME = "vaultadditions_PlayerStatData";
    private final Map<UUID, PlayerAdditionalVaultStats> playerMap = new HashMap<>();

    public PlayerAdditionalVaultStats getVaultStats(Player player) {
        return this.getVaultStats(player.getUUID());
    }

    public PlayerAdditionalVaultStats getVaultStats(UUID uuid) {
        return this.playerMap.computeIfAbsent(uuid, PlayerAdditionalVaultStats::new);
    }

    public PlayerAdditionalVaultStatData spendPowerPoints(ServerPlayer player, int amount) {
        this.getVaultStats(player).spendPowerPoints(player.getServer(), amount);
        this.setDirty();
        return this;
    }

    public PlayerAdditionalVaultStatData addPowerPoints(ServerPlayer player, int amount) {
        this.getVaultStats(player).addPowerPoints(amount).sync(player.getLevel().getServer());
        this.setDirty();
        return this;
    }

    public PlayerAdditionalVaultStatData refundPowerPoints(ServerPlayer player, int amount) {
        this.getVaultStats(player).refundPowerPoints(amount).sync(player.getLevel().getServer());
        this.setDirty();
        return this;
    }

    public PlayerAdditionalVaultStatData resetPowers(ServerPlayer player) {
        MinecraftServer server = player.getLevel().getServer();
        this.getVaultStats(player).resetPower(server).sync(server);
        PlayerAdditionalVaultStatData statsData = get(server);
        PlayerPowersData powerData = PlayerPowersData.get(server);
        PowerTree powerTree = powerData.getPowers(player);
        PlayerAdditionalVaultStats stats = statsData.getVaultStats(player);

        for (Skill skill : powerTree.getAll(LearnableSkill.class, Skill::isUnlocked)) {
            while (skill.isUnlocked()) {
                SkillContext context = SkillContext.of(player);
                if (skill.getParent() instanceof GroupedSkill grouped) {
                    grouped.select(skill.getId());
                    skill = grouped;
                }
                if (skill instanceof LearnableSkill learnable && learnable.canRegret(context)) {
                    learnable.regret(context);
                    powerTree.sync(context);
                }
            }
        }

        stats.setPowerPoints(0);
        stats.sync(server);
        this.setDirty();
        return this;
    }

    public PlayerAdditionalVaultStatData resetAndReturnPowerPoints(ServerPlayer player) {
        this.getVaultStats(player).resetAndReturnPowerPoints().sync(player.getLevel().getServer());
        this.setDirty();
        return this;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public static PlayerAdditionalVaultStatData getServer() {
        return get(ServerLifecycleHooks.getCurrentServer());
    }

    public static PlayerAdditionalVaultStatData get(ServerLevel world) {
        return get(world.getServer());
    }

    public static PlayerAdditionalVaultStatData get(MinecraftServer srv) {
        return srv.overworld().getDataStorage().computeIfAbsent(PlayerAdditionalVaultStatData::create, PlayerAdditionalVaultStatData::new, DATA_NAME);
    }

    private static PlayerAdditionalVaultStatData create(CompoundTag tag) {
        PlayerAdditionalVaultStatData data = new PlayerAdditionalVaultStatData();
        data.load(tag);
        return data;
    }

    public void load(CompoundTag nbt) {
        ListTag playerList = nbt.getList("PlayerEntries", Tag.TAG_STRING);
        ListTag statEntries = nbt.getList("StatEntries", Tag.TAG_COMPOUND);
        if (playerList.size() != statEntries.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
        } else {
            for (int i = 0; i < playerList.size(); ++i) {
                UUID playerUUID = UUID.fromString(playerList.getString(i));
                this.getVaultStats(playerUUID).deserializeNBT(statEntries.getCompound(i));
            }
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag nbt) {
        ListTag playerList = new ListTag();
        ListTag statsList = new ListTag();
        this.playerMap.forEach((uuid, stats) -> {
            playerList.add(StringTag.valueOf(uuid.toString()));
            statsList.add(stats.serializeNBT());
        });
        nbt.put("PlayerEntries", playerList);
        nbt.put("StatEntries", statsList);
        return nbt;
    }
}
