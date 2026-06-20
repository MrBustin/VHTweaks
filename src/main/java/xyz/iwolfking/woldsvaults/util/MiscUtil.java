package xyz.iwolfking.woldsvaults.util;

import xyz.iwolfking.woldsvaults.WoldsVaults;
import xyz.iwolfking.woldsvaults.data.PlayerAdditionalVaultStatData;
import xyz.iwolfking.woldsvaults.core.vault.PlayerAdditionalVaultStats;
import iskallia.vault.skill.PlayerVaultStats;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.source.SkillSource;
import iskallia.vault.util.function.ObservableSupplier;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

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
}
