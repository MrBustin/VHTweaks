package xyz.iwolfking.woldsvaults.util;

import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.source.SkillSource;
import iskallia.vault.util.function.ObservableSupplier;
import iskallia.vault.world.data.PlayerVaultStatsData;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.configs.overworld.VaultConfigRegistry;
import xyz.iwolfking.woldsvaults.core.vault.PlayerAdditionalVaultStats;
import xyz.iwolfking.woldsvaults.data.PlayerAdditionalVaultStatData;
import xyz.iwolfking.woldsvaults.init.overworld.OverworldVaultSounds;
import xyz.iwolfking.woldsvaults.network.overworld.ClientboundOverworldVaultCompassPacket;
import xyz.iwolfking.woldsvaults.network.overworld.OverworldVaultNetwork;
import xyz.iwolfking.woldsvaults.world.overworld.PortalData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiscUtil {
    private static final Map<String, Long> TIMES = new HashMap<>();
    public static final UUID sizeScaleModifierUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    public static int unspentPowerPoints;
    public static Component unspentPowerPointComponent;
    public static int unspentPowerPointComponentWidth;
    public static final ObservableSupplier<Integer> POWER_POINT_SUPPLIER = ObservableSupplier.of(() -> unspentPowerPoints, Integer::equals);

    public static SkillContext ofPowers(ServerPlayer player) {
        PlayerVaultStats stats = PlayerVaultStatsData.get(player.getLevel()).getVaultStats(player);
        PlayerAdditionalVaultStats additionalStats = PlayerAdditionalVaultStatData.get(player.getLevel()).getVaultStats(player);
        return new SkillContext(stats.getVaultLevel(), additionalStats.getUnspentPowerPoints(), SkillSource.of(player));
    }

    public static boolean limitedLog(String log, Object... args) {
        String toLog = log.formatted(args);
        if (TIMES.getOrDefault(toLog, 0L) < System.currentTimeMillis()) {
            TIMES.put(toLog, System.currentTimeMillis() + 5000);
            if (args.length > 0 && args[args.length - 1] instanceof Throwable t) {
                WoldsVaults.LOGGER.error(toLog, t);
            } else {
                WoldsVaults.LOGGER.info(toLog);
            }
            return true;
        }
        return false;
    }

    public static void sendCompassInfoToPlayer(ServerPlayer player, BlockPos pos) {
        sendCompassInfoToPlayer(player, player.getLevel().dimension(), pos);
    }

    public static void sendCompassInfoToPlayer(ServerPlayer player, ResourceKey<Level> dimension, BlockPos pos) {
        OverworldVaultNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new ClientboundOverworldVaultCompassPacket(dimension, pos)
        );
    }

    public static void clearCompassInfoForPlayer(ServerPlayer player) {
        OverworldVaultNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                ClientboundOverworldVaultCompassPacket.clear()
        );
    }

    public static void sendCompassInfo(ServerLevel level, BlockPos pos) {
        level.getPlayers(player -> !ServerVaults.isInVault(player.getLevel()))
                .forEach(player -> sendCompassInfoToPlayer(player, pos));
    }

    public static void sendCompassInfo(MinecraftServer server, ResourceKey<Level> dimension, BlockPos pos) {
        server.getPlayerList().getPlayers().stream()
                .filter(player -> !ServerVaults.isInVault(player.getLevel()))
                .forEach(player -> {
                    if (player.getLevel().dimension().equals(dimension)) {
                        sendCompassInfoToPlayer(player, dimension, pos);
                    } else {
                        clearCompassInfoForPlayer(player);
                    }
                });
    }

    public static void clearCompassInfo(ServerLevel level) {
        level.getPlayers(player -> !ServerVaults.isInVault(player.getLevel()))
                .forEach(MiscUtil::clearCompassInfoForPlayer);
    }

    public static void notifyPlayers(MinecraftServer server, PortalData data, String messageTemplate) {
        notifyPlayers(server, data, messageTemplate, data.getPortalFrameCenterPos());
    }

    public static void notifyPlayers(MinecraftServer server, PortalData data, String messageTemplate, BlockPos compassTarget) {
        server.getPlayerList().getPlayers().forEach(player -> {
            if (VaultConfigRegistry.OVERWORLD_VAULTS_GENERAL_CONFIG.PLAY_SOUND_ON_OPEN) {
                player.getLevel().playSound(null, player.blockPosition(), OverworldVaultSounds.PORTAL_SPAWN.get(), SoundSource.MASTER, 0.4f, 1.25f);
            }
        });

        if (VaultConfigRegistry.OVERWORLD_VAULTS_GENERAL_CONFIG.BROADCAST_IN_CHAT) {
            MiscUtil.broadcast(getPortalMessage(messageTemplate, data.getDimension()));

            if (VaultConfigRegistry.OVERWORLD_VAULTS_GENERAL_CONFIG.UPDATE_VAULT_COMPASS) {
                MiscUtil.sendCompassInfo(server, data.getDimension(), compassTarget);
            }
        }
    }

    public static Component getPortalMessage(String messageTemplate, ResourceKey<Level> dimension) {
        if (messageTemplate == null || messageTemplate.isBlank()) {
            return TextUtil.loginComponent();
        }

        if (isTranslationKey(messageTemplate)) {
            return new TranslatableComponent(messageTemplate, TextUtil.dimensionComponent(dimension));
        }

        try {
            return new TextComponent(String.format(messageTemplate, TextUtil.dimensionName(dimension)));
        } catch (Exception ignored) {
            return new TextComponent(messageTemplate);
        }
    }

    private static boolean isTranslationKey(String messageTemplate) {
        return !messageTemplate.contains(" ")
                && messageTemplate.indexOf('\u00A7') == -1
                && messageTemplate.contains(".")
                && !messageTemplate.contains("%");
    }

    public static int[] convertTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return new int[]{hours, minutes, seconds};
    }

    public static String formatTime(int[] time) {
        return String.format("%02d:%02d:%02d", time[0], time[1], time[2]);
    }

    public static void broadcast(Component message) {
        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        if (srv != null) {
            srv.getPlayerList().broadcastMessage(message, ChatType.SYSTEM, Util.NIL_UUID);
        }
    }
}
