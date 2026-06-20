package xyz.iwolfking.woldsvaults.api.util;

import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.prestige.core.PrestigePower;
import iskallia.vault.world.data.PlayerPrestigePowersData;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class PrestigePowerHelper {
    public static <T extends PrestigePower> List<T> getPrestigePowersOfType(ServerPlayer player, Class<T> powerType) {
        if(player.getServer() != null) {
            return PlayerPrestigePowersData.get(player.getServer()).getPowers(player).getAll(powerType, Skill::isUnlocked);
        }

        return List.of();
    }
}
