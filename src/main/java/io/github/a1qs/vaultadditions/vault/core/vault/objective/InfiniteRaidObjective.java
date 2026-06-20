package io.github.a1qs.vaultadditions.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.a1qs.vaultadditions.events.VaultCommonEvents;
import io.github.a1qs.vaultadditions.item.RaidPlaqueBlockItem;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.world.storage.VirtualWorld;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InfiniteRaidObjective extends Objective {
    public static final SupplierKey<Objective> KEY = SupplierKey.of("infinite_raid", Objective.class).with(Version.v1_21, InfiniteRaidObjective::new);
    public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
    public static final FieldKey<long[]> UUIDS = FieldKey.of("player_uuids", long[].class).with(Version.v1_21, Adapters.LONG_ARRAY, DISK.all()).register(FIELDS);
    public static final FieldKey<int[]> WAVES = FieldKey.of("player_waves", int[].class).with(Version.v1_21, Adapters.INT_ARRAY, DISK.all()).register(FIELDS);

    @Override
    public void initServer(VirtualWorld world, Vault vault) {
        VaultCommonEvents.RAID_WAVE_COMPLETED.register(this, data -> {
            if (data.world() == world) {
                Map<UUID, Integer> playerWaves = this.getPlayerWaves();
                playerWaves.compute(data.player().getUUID(), (uuid, waves) -> waves == null ? 1 : waves + 1);
                this.setPlayerWaves(playerWaves);
            }
        });
        CommonEvents.LISTENER_LEAVE.register(this, data -> {
            if (data.getVault() != vault) {
                return;
            }

            ServerPlayer player = data.getListener().getPlayer().orElse(null);
            if (player == null) {
                return;
            }

            Map<UUID, Integer> playerWaves = this.getPlayerWaves();
            Integer waves = playerWaves.remove(player.getUUID());
            if (waves == null) {
                return;
            }

            vault.ifPresent(Vault.STATS, stats -> {
                StatCollector stat = stats.get(data.getListener());
                if (stat != null) {
                    ItemStack plaque = RaidPlaqueBlockItem.create(player.getUUID(), player.getGameProfile().getName(), waves);
                    if (!plaque.isEmpty()) {
                        stat.get(StatCollector.REWARD).add(plaque);
                    }
                }
            });
        });
    }

    private void setPlayerWaves(Map<UUID, Integer> playerWaves) {
        long[] uuids = new long[playerWaves.size() * 2];
        int[] waves = new int[playerWaves.size()];
        int i = 0;
        for (Map.Entry<UUID, Integer> entry : playerWaves.entrySet()) {
            uuids[i] = entry.getKey().getMostSignificantBits();
            uuids[i + 1] = entry.getKey().getLeastSignificantBits();
            waves[i / 2] = entry.getValue();
            i += 2;
        }
        this.set(UUIDS, uuids);
        this.set(WAVES, waves);
    }

    private Map<UUID, Integer> getPlayerWaves() {
        Map<UUID, Integer> playerWaves = new HashMap<>();
        long[] uuids = this.getOr(UUIDS, new long[0]);
        int[] waves = this.getOr(WAVES, new int[0]);
        for (int i = 0; i < uuids.length; i += 2) {
            playerWaves.put(new UUID(uuids[i], uuids[i + 1]), waves[playerWaves.size()]);
        }
        return playerWaves;
    }

    @Override
    public boolean render(Vault vault, PoseStack poseStack, Window window, float v, Player player) {
        return false;
    }

    @Override
    public boolean isActive(VirtualWorld virtualWorld, Vault vault, Objective objective) {
        return objective == this;
    }

    @Override
    public FieldRegistry getFields() {
        return FIELDS;
    }

    @Override
    public SupplierKey<Objective> getKey() {
        return KEY;
    }
}
