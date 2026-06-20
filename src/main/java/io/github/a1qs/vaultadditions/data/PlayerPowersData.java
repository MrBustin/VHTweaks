package io.github.a1qs.vaultadditions.data;

import io.github.a1qs.vaultadditions.config.Configs;
import io.github.a1qs.vaultadditions.util.MiscUtil;
import io.github.a1qs.vaultadditions.vault.PlayerAdditionalVaultStats;
import io.github.a1qs.vaultadditions.vault.menu.PowerTree;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerPowersData extends SavedData {
    protected static final String DATA_NAME = "vaultadditions_PlayerPowers";
    private final Map<UUID, PowerTree> playerMap = new HashMap<>();
    private final Set<UUID> scheduledMerge = new HashSet<>();

    public PowerTree getPowers(Player player) {
        return this.getPowers(player.getUUID());
    }

    public PowerTree getPowers(UUID uuid) {
        return this.playerMap.computeIfAbsent(uuid, id -> Configs.POWERS.getTree().copy());
    }

    public void resetPowers(ServerPlayer player) {
        this.getPowers(player).iterate(LearnableSkill.class, skill -> skill.onRemove(SkillContext.of(player)));
        this.playerMap.remove(player.getUUID());
        this.getPowers(player).sync(SkillContext.of(player));
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side.isServer() && event.player instanceof ServerPlayer player) {
            PlayerPowersData data = get(player.getLevel());
            if (data.scheduledMerge.remove(player.getUUID())) {
                SkillContext context = MiscUtil.ofPowers(player);
                data.playerMap.put(player.getUUID(), (PowerTree) (data.playerMap.get(player.getUUID())).mergeFrom(Configs.POWERS.getTree().copy(), context));
                PlayerAdditionalVaultStats stats = PlayerAdditionalVaultStatData.get(player.getLevel()).getVaultStats(player);
                stats.setPowerPoints(context.getLearnPoints());
                PlayerAdditionalVaultStatData.get(player.getLevel()).setDirty();
                AttributeSnapshotHelper.getInstance().refreshSnapshotDelayed(player);
            }
            data.getPowers(player).onTick(SkillContext.of(player));
        }
    }

    private static PlayerPowersData create(CompoundTag tag) {
        PlayerPowersData data = new PlayerPowersData();
        data.load(tag);
        return data;
    }

    public void load(CompoundTag nbt) {
        this.playerMap.clear();
        this.scheduledMerge.clear();
        ListTag playerList = nbt.getList("Players", Tag.TAG_STRING);
        ListTag talentList = nbt.getList("Powers", Tag.TAG_COMPOUND);
        if (playerList.size() != talentList.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
        } else {
            for (int i = 0; i < playerList.size(); ++i) {
                UUID playerUUID = UUID.fromString(playerList.getString(i));
                Adapters.SKILL.readNbt(talentList.getCompound(i)).ifPresent((tree) -> {
                    this.playerMap.put(playerUUID, (PowerTree) tree);
                    this.scheduledMerge.add(playerUUID);
                });
            }
            this.setDirty();
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        ListTag playerList = new ListTag();
        ListTag talentList = new ListTag();
        for (Map.Entry<UUID, PowerTree> entry : this.playerMap.entrySet()) {
            UUID uuid = entry.getKey();
            PowerTree researchTree = entry.getValue();
            Adapters.SKILL.writeNbt(researchTree).ifPresent(tag -> {
                playerList.add(StringTag.valueOf(uuid.toString()));
                talentList.add(tag);
            });
        }
        nbt.put("Players", playerList);
        nbt.put("Powers", talentList);
        return nbt;
    }

    public static PlayerPowersData getServer() {
        return get(ServerLifecycleHooks.getCurrentServer());
    }

    public static PlayerPowersData get(ServerLevel world) {
        return get(world.getServer());
    }

    public static PlayerPowersData get(MinecraftServer srv) {
        PlayerPowersData data = srv.overworld().getDataStorage().computeIfAbsent(PlayerPowersData::create, PlayerPowersData::new, DATA_NAME);
        if (Configs.POWERS.isMergeScheduled()) {
            data.scheduledMerge.addAll(data.playerMap.keySet());
        }
        return data;
    }
}
