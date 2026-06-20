package xyz.iwolfking.woldsvaults.api.util;

import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.properties.CapacityCrystalProperties;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.iwolfking.woldsvaults.prestige.CrystalSizePrestigePower;

import java.util.List;

public class CrystalSizePowerHelper {
    public static boolean shouldNotConsumeCapacity(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        List<CrystalSizePrestigePower> prestigePowers =
                PrestigePowerHelper.getPrestigePowersOfType(serverPlayer, CrystalSizePrestigePower.class);
        if (prestigePowers.isEmpty()) {
            return false;
        }

        float sizeAvoidChance = 0.0f;
        for (CrystalSizePrestigePower power : prestigePowers) {
            sizeAvoidChance = Math.max(sizeAvoidChance, power.getSizeAvoidChance());
        }

        return sizeAvoidChance > 0.0f && serverPlayer.getRandom().nextFloat() < sizeAvoidChance;
    }

    public static void refundCapacityCost(ItemStack output, int catalystSize) {
        if (output.isEmpty() || catalystSize <= 0) {
            return;
        }

        CrystalData data = CrystalData.read(output);
        if (!(data.getProperties() instanceof CapacityCrystalProperties properties)) {
            return;
        }

        properties.setSize(Math.max(0, properties.getSize() - catalystSize));
        data.write(output);
    }
}
